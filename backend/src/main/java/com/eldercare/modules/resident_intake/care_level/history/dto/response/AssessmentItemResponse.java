package com.eldercare.modules.resident_intake.care_level.history.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssessmentItemResponse {

    private Long metricId;

    private String category;

    private String metricName;

    private Integer score;

    private String notes;
}