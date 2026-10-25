package com.eldercare.modules.demo.seeder;

import com.eldercare.modules.demo.dto.csv.ResidentCsvRow;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ResidentCsvReader {

    public List<ResidentCsvRow> read(MultipartFile file) {
        List<ResidentCsvRow> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) continue;
                if (isHeaderLine(line)) continue;

                String[] columns = splitCsvOrTsvLine(line);

                if (columns.length < 14) {
                    throw new RuntimeException("Invalid CSV row at line " + lineNumber);
                }

                ResidentCsvRow row = new ResidentCsvRow(
                        parseNullableString(columns[0]),
                        parseNullableString(columns[1]),
                        parseNullableString(columns[2]),
                        parseNullableString(columns[3]),
                        normalizeGender(columns[4]),
                        parseNullableString(columns[5]),
                        parseNullableString(columns[6]),
                        normalizeResidentStatus(columns[7]),
                        parseNullableString(columns[8]),
                        parseNullableString(columns[9]),
                        parseNullableString(columns[10]),
                        parseNullableString(columns[11]),
                        parseIntOrDefault(columns[12], 0),
                        parseIntOrDefault(columns[13], 0)
                );

                validate(row, lineNumber);
                rows.add(row);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }

        return rows;
    }

    private void validate(ResidentCsvRow row, int lineNumber) {
        if (row.firstName() == null || row.firstName().isBlank()) {
            throw new RuntimeException("first_name is required at line " + lineNumber);
        }

        if (row.lastName() == null || row.lastName().isBlank()) {
            throw new RuntimeException("last_name is required at line " + lineNumber);
        }

        try {
            LocalDate.parse(row.dateOfBirth());
        } catch (Exception e) {
            throw new RuntimeException("Invalid date_of_birth at line " + lineNumber + ": " + row.dateOfBirth());
        }
    }

    private boolean isHeaderLine(String line) {
        return removeBom(line).toLowerCase().startsWith("first_name,");
    }

    private String[] splitCsvOrTsvLine(String line) {
        return line.contains("\t") ? line.split("\\t", -1) : line.split(",", -1);
    }

    private String parseNullableString(String value) {
        if (value == null) return null;

        String trimmed = removeBom(value).trim();

        if (trimmed.isBlank() || trimmed.equalsIgnoreCase("NULL")) return null;

        return trimmed;
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        String trimmed = parseNullableString(value);
        return trimmed == null ? defaultValue : Integer.parseInt(trimmed);
    }

    private String removeBom(String value) {
        if (value == null) return null;
        return value.replace("\uFEFF", "");
    }

    private String normalizeGender(String gender) {
        String value = parseNullableString(gender);

        if (value == null) return "UNDISCLOSED";

        return switch (value.trim().toUpperCase()) {
            case "MALE", "M" -> "MALE";
            case "FEMALE", "F" -> "FEMALE";
            case "OTHER" -> "OTHER";
            default -> "UNDISCLOSED";
        };
    }

    private String normalizeResidentStatus(String status) {
        String value = parseNullableString(status);

        if (value == null) return "ACTIVE";

        return switch (value.trim().toUpperCase()) {
            case "ACTIVE" -> "ACTIVE";
            case "INACTIVE" -> "INACTIVE";
            case "DECEASED" -> "DECEASED";
            case "DISCHARGED" -> "DISCHARGED";
            default -> "ACTIVE";
        };
    }
}
