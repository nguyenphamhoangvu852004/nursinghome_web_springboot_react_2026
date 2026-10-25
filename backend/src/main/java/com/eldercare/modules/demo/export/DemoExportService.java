package com.eldercare.modules.demo.export;

import com.eldercare.modules.demo.dto.export.IncidentExportRow;
import com.eldercare.modules.demo.dto.export.ResidentExportRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoExportService {

    private final JdbcTemplate jdbcTemplate;
    private final CsvExportBuilder csvExportBuilder;
    private final PdfExportBuilder pdfExportBuilder;

    public byte[] export(String type, String format) {
        String normalizedType = type.trim();
        String normalizedFormat = format.trim().toLowerCase();

        if ("ResidentList".equals(normalizedType)) {
            List<ResidentExportRow> rows = getResidentExportRows();
            return "csv".equals(normalizedFormat)
                    ? csvExportBuilder.buildResidentCsv(rows)
                    : pdfExportBuilder.buildResidentPdf(rows);
        }

        if ("IncidentLog".equals(normalizedType)) {
            List<IncidentExportRow> rows = getIncidentExportRows();
            return "csv".equals(normalizedFormat)
                    ? csvExportBuilder.buildIncidentCsv(rows)
                    : pdfExportBuilder.buildIncidentPdf(rows);
        }

        throw new RuntimeException("Invalid export type: " + type);
    }

    private List<ResidentExportRow> getResidentExportRows() {
        return jdbcTemplate.query(
                """
                SELECT id, first_name, middle_name, last_name, date_of_birth,
                       gender, marital_status, religion_preference, status,
                       is_chart_locked, bed_id, created_at
                FROM residents
                ORDER BY id
                """,
                (rs, rowNum) -> new ResidentExportRow(
                        rs.getLong("id"),
                        rs.getString("first_name"),
                        rs.getString("middle_name"),
                        rs.getString("last_name"),
                        rs.getString("date_of_birth"),
                        rs.getString("gender"),
                        rs.getString("marital_status"),
                        rs.getString("religion_preference"),
                        rs.getString("status"),
                        rs.getBoolean("is_chart_locked"),
                        rs.getObject("bed_id") == null ? null : rs.getLong("bed_id"),
                        rs.getString("created_at")
                )
        );
    }

    private List<IncidentExportRow> getIncidentExportRows() {
        return jdbcTemplate.query(
                """
                SELECT id, incident_type, status, description, sla_deadline,
                       resident_id, severity_id, reported_by, reported_at
                FROM incidents
                ORDER BY reported_at DESC
                """,
                (rs, rowNum) -> new IncidentExportRow(
                        rs.getLong("id"),
                        rs.getString("incident_type"),
                        rs.getString("status"),
                        rs.getString("description"),
                        rs.getString("sla_deadline"),
                        rs.getLong("resident_id"),
                        rs.getLong("severity_id"),
                        rs.getLong("reported_by"),
                        rs.getString("reported_at")
                )
        );
    }
}
