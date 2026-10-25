package com.eldercare.modules.resident_intake.assessment.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentCreateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentDecisionRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentUpdateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.response.AssessmentResponse;
import com.eldercare.modules.resident_intake.assessment.service.AssessmentService;
import com.eldercare.modules.resident_intake.assessment_metric.dto.AssessmentMetricDTO;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.LocClassificationResultResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentController {

        private final AssessmentService service;

        @GetMapping("/metrics")
        public ResponseEntity<ApiResponse<List<AssessmentMetricDTO>>> metrics() {
                return ResponseEntity.ok(ApiResponse.success(service.getMetrics()));
        }

        @PostMapping
        public ResponseEntity<ApiResponse<AssessmentResponse>> create(@RequestBody AssessmentCreateRequest req) {
                AssessmentResponse response = service.create(req);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.created("Assessment created successfully", response));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApiResponse<AssessmentResponse>> update(
                        @PathVariable Long id, @RequestBody AssessmentUpdateRequest req) {
                return ResponseEntity
                                .ok(ApiResponse.success("Assessment updated successfully", service.update(id, req)));
        }

        @GetMapping
        public ResponseEntity<PagedResponse<List<AssessmentResponse>>> list(
                        @PageableDefault(size = 10) Pageable pageable) {
                Page<AssessmentResponse> page = service.listPaged(pageable);
                return ResponseEntity.ok(
                                PagedResponse.of(page.getContent(), 200, "Success",
                                                page.getNumber(), page.getTotalPages(), page.getSize(),
                                                page.getTotalElements()));
        }

        @PutMapping("/{id}/decision")
        public ResponseEntity<ApiResponse<AssessmentResponse>> decide(
                        @PathVariable Long id, @RequestBody AssessmentDecisionRequest req) {
                return ResponseEntity
                                .ok(ApiResponse.success("Assessment updated successfully", service.decide(id, req)));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
                service.deleteDraft(id);
                return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
        }

        @GetMapping("/resident/{residentId}/classification-result")
        public ResponseEntity<ApiResponse<LocClassificationResultResponse>> getClassificationResult(
                        @PathVariable Long residentId) {
                return ResponseEntity.ok(ApiResponse.success(service.getClassificationResult(residentId)));
        }
}