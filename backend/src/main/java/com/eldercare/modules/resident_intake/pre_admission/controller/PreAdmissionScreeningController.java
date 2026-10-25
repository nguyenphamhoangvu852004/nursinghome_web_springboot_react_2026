package com.eldercare.modules.resident_intake.pre_admission.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreCreateRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreDecisionRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreResponse;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreSelectDTO;
import com.eldercare.modules.resident_intake.pre_admission.service.PreAdmissionScreeningService;
import com.eldercare.modules.resident_intake.resident.dto.response.ResidentPendingDTO;
import com.eldercare.modules.resident_intake.resident.service.ResidentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/pre-admissions")
@RequiredArgsConstructor
public class PreAdmissionScreeningController {

    private final PreAdmissionScreeningService service;
    private final ResidentService residentService;

    @GetMapping("/pending-residents")
    public ResponseEntity<ApiResponse<List<ResidentPendingDTO>>> pendingResidents() {
        return ResponseEntity.ok(
                ApiResponse.success(residentService.getPendingResidents()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PreResponse>> create(
            @RequestBody PreCreateRequest req) {

        PreResponse response = service.create(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(
                        "Pre-admission screening created successfully",
                        response));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<List<PreResponse>>> list(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<PreResponse> page = service.listPaged(pageable);

        return ResponseEntity.ok(
                PagedResponse.of(
                        page.getContent(),
                        200,
                        "Success",
                        page.getNumber(),
                        page.getTotalPages(),
                        page.getSize(),
                        page.getTotalElements()));
    }

    @GetMapping("/select-completed")
    public ResponseEntity<ApiResponse<List<PreSelectDTO>>> selectCompleted() {

        return ResponseEntity.ok(
                ApiResponse.success(service.listCompletedForSelect()));
    }

    @PutMapping("/{id}/decision")
    public ResponseEntity<ApiResponse<PreResponse>> decide(
            @PathVariable Long id,
            @RequestBody PreDecisionRequest req) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pre-admission screening updated successfully",
                        service.decide(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.deleteDraft(id);
        return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
    }
}