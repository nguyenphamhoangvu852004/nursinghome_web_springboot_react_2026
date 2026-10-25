package com.eldercare.modules.risk_incident.incident_tracking.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
public class CreateIncidentSeverityRequest {

    @JsonProperty("level_name")
    @NotBlank(message = "level_name is required")
    private String levelName;

    @JsonProperty("chart_lock_trigger")
    @NotNull(message = "chart_lock_trigger is required")
    private Boolean chartLockTrigger;

    @JsonProperty("description")
    private String description;

    @JsonProperty("example")
    private String example; 
}
