package com.eldercare.modules.human_resources.staffing_compliance.staffing.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffingRatioResponse {

    private Long id;

    @JsonProperty("facility_id")
    private Long facilityId;

    @JsonProperty("min_hrs_per_resident_day")
    private BigDecimal minHrsPerResidentDay;

    @JsonProperty("warn_below_percentage")
    private Integer warnBelowPercentage;
}
