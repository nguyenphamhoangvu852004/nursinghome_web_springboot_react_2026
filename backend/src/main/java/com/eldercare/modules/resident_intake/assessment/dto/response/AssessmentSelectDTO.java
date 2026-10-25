package com.eldercare.modules.resident_intake.assessment.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentSelectDTO {
  private Long id;
  private String residentName;
  private Integer adlTotalScore;
}
