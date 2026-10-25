package com.eldercare.modules.resident_intake.assessment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentDecisionRequest {
  private String status;
  private Long confirmedCareLevelId;
  private String overrideReason;
}
