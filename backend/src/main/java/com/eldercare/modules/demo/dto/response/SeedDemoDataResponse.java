package com.eldercare.modules.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeedDemoDataResponse {

    @JsonProperty("seed_job_id")
    private Long seedJobId;

    private String status;

    @JsonProperty("residents_loaded")
    private Integer residentsLoaded;

    @JsonProperty("medication_orders_loaded")
    private Integer medicationOrdersLoaded;

    @JsonProperty("incidents_loaded")
    private Integer incidentsLoaded;
}
