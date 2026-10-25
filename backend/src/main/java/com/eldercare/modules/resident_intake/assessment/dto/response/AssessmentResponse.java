package com.eldercare.modules.resident_intake.assessment.dto.response;

import java.util.List;

import com.eldercare.modules.resident_intake.assessment_detail.dto.response.AssessmentDetailResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentResponse {
  private Long id;
  private String status;
  private Integer adlTotalScore;
  private Long residentId;
  private Long suggestedCareLevelId;
  private String residentName;
  private Boolean isOverridden;
  private Long admissionId;
  private String overrideReason;
  private List<AssessmentDetailResponse> details;
}