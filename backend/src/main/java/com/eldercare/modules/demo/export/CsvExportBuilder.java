package com.eldercare.modules.demo.export;

import com.eldercare.modules.demo.dto.export.IncidentExportRow;
import com.eldercare.modules.demo.dto.export.ResidentExportRow;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExportBuilder {

    public byte[] buildResidentCsv(List<ResidentExportRow> rows) {
        StringBuilder csv = new StringBuilder();

        csv.append("id,first_name,middle_name,last_name,date_of_birth,gender,marital_status,religion_preference,status,is_chart_locked,bed_id,created_at\n");

        for (ResidentExportRow row : rows) {
            csv.append(row.id()).append(",")
                    .append(csvValue(row.firstName())).append(",")
                    .append(csvValue(row.middleName())).append(",")
                    .append(csvValue(row.lastName())).append(",")
                    .append(csvValue(row.dateOfBirth())).append(",")
                    .append(csvValue(row.gender())).append(",")
                    .append(csvValue(row.maritalStatus())).append(",")
                    .append(csvValue(row.religionPreference())).append(",")
                    .append(csvValue(row.status())).append(",")
                    .append(row.chartLocked()).append(",")
                    .append(row.bedId() == null ? "" : row.bedId()).append(",")
                    .append(csvValue(row.createdAt()))
                    .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] buildIncidentCsv(List<IncidentExportRow> rows) {
        StringBuilder csv = new StringBuilder();

        csv.append("id,incident_type,status,description,sla_deadline,resident_id,severity_id,reported_by,reported_at\n");

        for (IncidentExportRow row : rows) {
            csv.append(row.id()).append(",")
                    .append(csvValue(row.incidentType())).append(",")
                    .append(csvValue(row.status())).append(",")
                    .append(csvValue(row.description())).append(",")
                    .append(csvValue(row.slaDeadline())).append(",")
                    .append(row.residentId()).append(",")
                    .append(row.severityId()).append(",")
                    .append(row.reportedBy()).append(",")
                    .append(csvValue(row.reportedAt()))
                    .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csvValue(String value) {
        if (value == null) return "";

        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}
