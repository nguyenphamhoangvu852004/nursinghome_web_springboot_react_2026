package com.eldercare.modules.risk_incident.incident_tracking.dto.request;


import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;


import java.time.LocalDateTime;


public record CreateIncidentRequest(

        @NotNull(message = "residentID is required")
        Long residentID,

        @NotNull(message = "incidentType is required")
        IncidentType incidentType,

        @NotNull(message = "severityID is required")
        Long severityID,

        @NotNull(message = "occurredAt is required")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime occurredAt,
        String location,
        String description,
        String witnesses

) {
}