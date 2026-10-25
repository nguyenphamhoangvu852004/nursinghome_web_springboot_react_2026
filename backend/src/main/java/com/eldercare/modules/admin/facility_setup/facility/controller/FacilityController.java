package com.eldercare.modules.admin.facility_setup.facility.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityCreateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityUpdateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilityResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilitySelectResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RouteConstants.API_ADMIN_FACILITIES)
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<PagedResponse<List<FacilityResponse>>> getFacilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(facilityService.getFacilities(page, size, search));
    }

    @PostMapping
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<FacilityResponse> createFacility(@RequestBody FacilityCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facilityService.createFacility(request));
    }

    @GetMapping("/{facilityId}")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<FacilityResponse> getFacilityInfo(@PathVariable Long facilityId) {
        return ResponseEntity.ok(facilityService.getFacilityInfo(facilityId));
    }

    @PutMapping("/{facilityId}")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<FacilityResponse> updateFacilityInfo(@PathVariable Long facilityId,
            @RequestBody FacilityUpdateRequest request) {
        return ResponseEntity.ok(facilityService.updateFacilityInfo(facilityId, request));
    }

    @GetMapping("/select")
    public ResponseEntity<ApiResponse<List<FacilitySelectResponse>>> getFacilitiesForSelect() {
        return ResponseEntity.ok(
                ApiResponse.success(facilityService.getFacilitiesForSelect()));
    }
}
