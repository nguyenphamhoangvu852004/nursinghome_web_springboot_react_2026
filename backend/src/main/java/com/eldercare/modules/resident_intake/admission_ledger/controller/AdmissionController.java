package com.eldercare.modules.resident_intake.admission_ledger.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionCreateRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionDischargeRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionResponse;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionSelectDTO;
import com.eldercare.modules.resident_intake.admission_ledger.service.AdmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admissions")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionService service;

    @GetMapping
    public ResponseEntity<PagedResponse<List<AdmissionResponse>>> list(
            @PageableDefault(size = 10) Pageable pageable) {

        Page<AdmissionResponse> page = service.listPaged(pageable);

        return ResponseEntity.ok(
                PagedResponse.of(
                        page.getContent(), 200, "Success",
                        page.getNumber(), page.getTotalPages(), page.getSize(), page.getTotalElements()));
    }

    @GetMapping("/select-active")
    public ResponseEntity<ApiResponse<List<AdmissionSelectDTO>>> selectActive() {
        return ResponseEntity.ok(ApiResponse.success(service.listActiveForSelect()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdmissionResponse>> create(
            @RequestBody AdmissionCreateRequest req) {

        AdmissionResponse response = service.create(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Admission created successfully", response));
    }

    @PutMapping("/{id}/discharge")
    public ResponseEntity<ApiResponse<AdmissionResponse>> discharge(
            @PathVariable Long id,
            @RequestBody AdmissionDischargeRequest req) {

        return ResponseEntity.ok(
                ApiResponse.success("Resident discharged successfully", service.discharge(id, req)));
    }
}