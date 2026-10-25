package com.eldercare.modules.resident_intake.resident.controller;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.resident_intake.resident.dto.request.*;
import com.eldercare.modules.resident_intake.resident.dto.response.*;
import com.eldercare.modules.resident_intake.resident.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/residents")
@CrossOrigin(origins = "http://localhost:5173")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    // 10. getResidents: GET /api/v1/residents
    @GetMapping
    public ResponseEntity<ApiResponse<ResidentListResponseContainerDto>> getResidents(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, name = "bed_id") Long bedId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10", name = "page_size") Integer pageSize) {

        ResidentListResponseContainerDto data = residentService.getResidentsV1(status, bedId, search, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // 11. getResidentById: GET /api/v1/residents/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, ResidentResponseDto>>> getResidentById(@PathVariable Long id) {
        ResidentResponseDto detail = residentService.getResidentByIdV1(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of("resident", detail)));
    }

    // 12. createResident: POST /api/v1/residents
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, ResidentResponseDto>>> createResident(
            @RequestBody ResidentCreateRequestDto dto) {
        ResidentResponseDto created = residentService.createResidentV1(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Success", Map.of("resident", created)));
    }

    // 13. updateResident: PATCH /api/v1/residents/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateResident(
            @PathVariable Long id,
            @RequestBody ResidentSaveRequestDto dto) {
        residentService.saveResident(id, dto);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 14. updateResidentStatus: PATCH /api/v1/residents/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, ResidentResponseDto>>> updateResidentStatus(
            @PathVariable Long id,
            @RequestBody ResidentStatusUpdateRequestDto dto) {
        ResidentResponseDto updated = residentService.updateResidentStatusV1(id, dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("resident", updated)));
    }

    // 15. assignResidentBed: PATCH /api/v1/residents/{id}/assign-bed
    @PatchMapping("/{id}/assign-bed")
    public ResponseEntity<ApiResponse<Map<String, ResidentResponseDto>>> assignResidentBed(
            @PathVariable Long id,
            @RequestBody ResidentAssignBedRequestDto dto) {
        ResidentResponseDto updated = residentService.assignResidentBedV1(id, dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("resident", updated)));
    }

    // 16. lockResidentChart: PATCH /api/v1/residents/{id}/lock-chart
    @PatchMapping("/{id}/lock-chart")
    public ResponseEntity<ApiResponse<Map<String, ResidentResponseDto>>> lockResidentChart(
            @PathVariable Long id,
            @RequestBody ResidentChartLockRequestDto dto) {
        ResidentResponseDto updated = residentService.lockResidentChartV1(id, dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("resident", updated)));
    }

    // 17. unlockResidentChart: PATCH /api/v1/residents/{id}/unlock-chart
    @PatchMapping("/{id}/unlock-chart")
    public ResponseEntity<ApiResponse<Map<String, ResidentResponseDto>>> unlockResidentChart(
            @PathVariable Long id,
            @RequestBody ResidentChartLockRequestDto dto) {
        ResidentResponseDto updated = residentService.unlockResidentChartV1(id, dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("resident", updated)));
    }

    // 18. deleteResident: DELETE /api/v1/residents/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResident(@PathVariable Long id) {
        ApiResponse<Void> response = ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Hard delete is not supported via public API due to PHI retention requirements.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<ApiResponse<ResidentInfoResponseDto>> getResidentInfo(@PathVariable Long id) {
        ResidentInfoResponseDto info = residentService.getResidentInfo(id);
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    // @GetMapping("/{id}/contacts")
    // public ResponseEntity<ApiResponse<List<ResidentContactResponseDto>>>
    // getResidentContacts(@PathVariable Long id) {
    // List<ResidentContactResponseDto> contacts =
    // residentService.getResidentContacts(id);
    // return ResponseEntity.ok(ApiResponse.success(contacts));
    // }

    // @GetMapping("/{id}/care-level-history")
    // public ResponseEntity<ApiResponse<List<ResidentCareLevelHistoryResponseDto>>> getResidentCareLevelHistory(@PathVariable Long id) {
    //     List<ResidentCareLevelHistoryResponseDto> history = residentService.getResidentCareLevelHistory(id);
    //     return ResponseEntity.ok(ApiResponse.success(history));
    // }

    @GetMapping("/{id}/sensitive-info")
    public ResponseEntity<ApiResponse<ResidentSensitiveInfoResponseDto>> getResidentSensitiveInfo(
            @PathVariable Long id) {
        ResidentSensitiveInfoResponseDto sensitive = residentService.getResidentSensitiveInfo(id);
        return ResponseEntity.ok(ApiResponse.success(sensitive));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<ResidentDetailResponseDto>> getResidentDetail(@PathVariable Long id) {
        ResidentDetailResponseDto detail = residentService.getResidentDetail(id);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }
}
