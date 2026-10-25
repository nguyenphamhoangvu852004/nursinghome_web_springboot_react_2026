package com.eldercare.modules.risk_incident.incident_tracking.dto.reponse;

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
public class IncidentSeverityResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("level_name")
    private String levelName;

    @JsonProperty("chart_lock_trigger")
    private Boolean chartLockTrigger;

    @JsonProperty("description")
    private String description;

    @JsonProperty("example")
    private String example;
}
