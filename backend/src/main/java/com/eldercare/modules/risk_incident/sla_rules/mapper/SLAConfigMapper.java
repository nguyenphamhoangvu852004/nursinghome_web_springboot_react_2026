package com.eldercare.modules.risk_incident.sla_rules.mapper;

import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;
import com.eldercare.modules.risk_incident.sla_rules.dto.CreateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.dto.SLAResponse;
import com.eldercare.modules.risk_incident.sla_rules.dto.UpdateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.entity.SLAConfigEntity;

public interface SLAConfigMapper {

    static SLAResponse toResponse(SLAConfigEntity slaConfig) {
        if (slaConfig == null) {
            return null;
        }

        return SLAResponse.builder()
                .id(slaConfig.getId())
                .severityId(slaConfig.getSeverity().getId())
                .slaWindowHrs(slaConfig.getSlaWindowHrs())
                .externalReportRequired(slaConfig.getExternalReportRequired())
                .regulatoryBody(slaConfig.getRegulatoryBody())
                .build();
    }

    static SLAConfigEntity toEntity(CreateSLARequest request, IncidentSeverityEntity severity) {
        if (request == null || severity == null) {
            return null;
        }

        return SLAConfigEntity.builder()
                .slaWindowHrs(request.getSlaWindowHrs())
                .severity(severity)
                .externalReportRequired(request.getExternalReportRequired())
                .regulatoryBody(request.getRegulatoryBody())
                .build();
    }

    static void updateEntity(SLAConfigEntity target, UpdateSLARequest request, IncidentSeverityEntity severity) {
        if (target == null || request == null || severity == null) {
            return;
        }

        target.setSlaWindowHrs(request.getSlaWindowHrs());
        target.setSeverity(severity);
        target.setExternalReportRequired(request.getExternalReportRequired());
        target.setRegulatoryBody(request.getRegulatoryBody());
    }
}
