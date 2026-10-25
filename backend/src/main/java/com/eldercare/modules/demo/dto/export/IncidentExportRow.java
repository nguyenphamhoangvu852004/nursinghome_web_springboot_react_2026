package com.eldercare.modules.demo.dto.export;

public record IncidentExportRow(
        Long id,
        String incidentType,
        String status,
        String description,
        String slaDeadline,
        Long residentId,
        Long severityId,
        Long reportedBy,
        String reportedAt
) {
}
