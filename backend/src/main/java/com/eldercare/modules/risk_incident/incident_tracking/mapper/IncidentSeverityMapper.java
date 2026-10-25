package com.eldercare.modules.risk_incident.incident_tracking.mapper;

import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentSeverityResponse;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.UpdateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;

public interface IncidentSeverityMapper {

    static IncidentSeverityResponse toResponse(IncidentSeverityEntity incidentSeverity) {
        if (incidentSeverity == null) {
            return null;
        }

        return IncidentSeverityResponse.builder()
                .id(incidentSeverity.getId())
                .levelName(incidentSeverity.getLevelName())
                .chartLockTrigger(incidentSeverity.getChartLockTrigger())
                .description(incidentSeverity.getDescription())
                .example(incidentSeverity.getExample())
                .build();
    }

    static IncidentSeverityEntity toEntity(CreateIncidentSeverityRequest request) {
        if (request == null) {
            return null;
        }

        return IncidentSeverityEntity.builder()
                .levelName(request.getLevelName())
                .chartLockTrigger(request.getChartLockTrigger())
                .description(request.getDescription())
                .example(request.getExample())
                .build();
    }

    static void updateEntity(IncidentSeverityEntity target, UpdateIncidentSeverityRequest request) {
        if (target == null || request == null) {
            return;
        }

        target.setLevelName(request.getLevelName());
        target.setChartLockTrigger(request.getChartLockTrigger());
        target.setDescription(request.getDescription());
        target.setExample(request.getExample());
    }
}
