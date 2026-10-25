package com.eldercare.modules.admin.facility_setup.care_level.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCareLevelRateRequest {

    @JsonProperty("care_level_id")
    private Long careLevelId;

    @JsonProperty("facility_id")
    private Long facilityId;

    @JsonProperty("daily_rate")
    private BigDecimal dailyRate;

    @JsonProperty("effective_from")
    private LocalDate effectiveFrom;

    @JsonProperty("effective_to")
    private LocalDate effectiveTo;

}

