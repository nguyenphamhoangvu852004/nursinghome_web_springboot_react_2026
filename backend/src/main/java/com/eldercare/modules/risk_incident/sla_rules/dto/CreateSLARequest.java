package com.eldercare.modules.risk_incident.sla_rules.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSLARequest {

    @JsonProperty("severity_id")
    @NotNull(message = "severity_id is required")
    private Long severityId;

    @JsonProperty("sla_window_hrs")
    @NotNull(message = "sla_window_hrs is required")
    private Integer slaWindowHrs;

    @JsonProperty("external_report_required")
    private Boolean externalReportRequired;

    @JsonProperty("regulatory_body")
    private String regulatoryBody;
}
