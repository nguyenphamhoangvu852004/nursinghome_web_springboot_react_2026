package com.eldercare.modules.resident_intake.assessment.dto.request;

import java.util.List;

import com.eldercare.modules.resident_intake.assessment_detail.dto.request.AssessmentDetailRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentCreateRequest {
  private Long admissionId;
  private List<AssessmentDetailRequest> details;
}