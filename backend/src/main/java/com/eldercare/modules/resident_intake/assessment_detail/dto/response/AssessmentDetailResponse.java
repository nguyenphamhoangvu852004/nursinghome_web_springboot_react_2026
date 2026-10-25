package com.eldercare.modules.resident_intake.assessment_detail.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentDetailResponse {
  private Long metricId;
  private String metricName;
  private String category;
  private Integer score;
  private String notes;
}
