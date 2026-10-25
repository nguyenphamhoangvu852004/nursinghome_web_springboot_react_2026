package com.eldercare.modules.resident_intake.assessment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentCreateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentDecisionRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentUpdateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.response.AssessmentResponse;
import com.eldercare.modules.resident_intake.assessment_metric.dto.AssessmentMetricDTO;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.LocClassificationResultResponse;

public interface AssessmentService {

  List<AssessmentMetricDTO> getMetrics();

  AssessmentResponse create(AssessmentCreateRequest request);

  AssessmentResponse update(Long id, AssessmentUpdateRequest request);

  Page<AssessmentResponse> listPaged(Pageable pageable);

  AssessmentResponse decide(Long id, AssessmentDecisionRequest request);

  LocClassificationResultResponse getClassificationResult(Long residentId);

  void deleteDraft(Long id);
}