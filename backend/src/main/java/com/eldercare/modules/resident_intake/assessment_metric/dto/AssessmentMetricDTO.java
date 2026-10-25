package com.eldercare.modules.resident_intake.assessment_metric.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentMetricDTO {
  private Long id;
  private String category;
  private String metricName;
}
