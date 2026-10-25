package com.eldercare.modules.risk_incident.sla_rules.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SLAResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("severity_id")
    private Long severityId;

    @JsonProperty("sla_window_hrs")
    private Integer slaWindowHrs;

    @JsonProperty("external_report_required")
    private Boolean externalReportRequired;

    @JsonProperty("regulatory_body")
    private String regulatoryBody;
}
