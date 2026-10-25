package com.eldercare.modules.demo.export;

import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.demo.dto.export.IncidentExportRow;
import com.eldercare.modules.demo.dto.export.ResidentExportRow;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PdfExportBuilder {

    public byte[] buildResidentPdf(List<ResidentExportRow> rows) {
        StringBuilder content = new StringBuilder();

        content.append("ResidentEntity List Export\n\n");

        for (ResidentExportRow row : rows) {
            content.append("ID: ").append(row.id()).append("\n");
            content.append("Name: ")
                    .append(row.firstName()).append(" ")
                    .append(row.middleName() == null ? "" : row.middleName() + " ")
                    .append(row.lastName()).append("\n");
            content.append("DOB: ").append(row.dateOfBirth()).append("\n");
            content.append("Gender: ").append(row.gender()).append("\n");
            content.append("Status: ").append(row.status()).append("\n");
            content.append("-----------------------------\n");
        }

        return content.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] buildIncidentPdf(List<IncidentExportRow> rows) {
        StringBuilder content = new StringBuilder();

        content.append("Incident Log Export\n\n");

        for (IncidentExportRow row : rows) {
            content.append("ID: ").append(row.id()).append("\n");
            content.append("Type: ").append(row.incidentType()).append("\n");
            content.append("Status: ").append(row.status()).append("\n");
            content.append("Description: ").append(row.description()).append("\n");
            content.append("ResidentEntity ID: ").append(row.residentId()).append("\n");
            content.append("Reported At: ").append(row.reportedAt()).append("\n");
            content.append("-----------------------------\n");
        }

        return content.toString().getBytes(StandardCharsets.UTF_8);
    }
}   
