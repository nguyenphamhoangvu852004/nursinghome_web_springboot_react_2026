package com.eldercare.modules.human_resources.staffing_compliance.staffing.controller;

import com.eldercare.modules.human_resources.staffing_compliance.staffing.dto.request.UpdateStaffingRatioRequest;
import com.eldercare.modules.human_resources.staffing_compliance.staffing.dto.response.StaffingRatioResponse;
import com.eldercare.modules.human_resources.staffing_compliance.staffing.service.StaffingRatioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/admin/staffing-ratio-config")
@RequiredArgsConstructor
public class StaffingRatioController {

    private final StaffingRatioService staffingRatioService;

    @GetMapping
    public ResponseEntity<StaffingRatioResponse> getStaffingRatio() {
        return ResponseEntity.ok(staffingRatioService.getStaffingRatio());
    }

    @PutMapping
    public ResponseEntity<StaffingRatioResponse> updateStaffingRatio(
            @RequestBody UpdateStaffingRatioRequest request
    ) {
        return ResponseEntity.ok(staffingRatioService.updateStaffingRatio(request));
    }
}
