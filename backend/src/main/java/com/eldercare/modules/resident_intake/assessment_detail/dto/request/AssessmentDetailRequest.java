package com.eldercare.modules.resident_intake.assessment_detail.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentDetailRequest {
  private Long metricId;
  private Integer score;
  private String notes;
}
