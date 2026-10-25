package com.eldercare.modules.resident_intake.care_level.history.controller;


import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eldercare.modules.resident_intake.care_level.history.dto.request.TransitionResidentCareLevelRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.request.UpdateResidentCareLevelHistoryRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.ActiveCareLevelSummaryResponse;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.ResidentCareLevelHistoryResponse;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.TransitionResidentCareLevelResponse;
import com.eldercare.modules.resident_intake.care_level.history.service.ResidentCareLevelHistoryService;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResidentCareLevelHistoryController {


    private final ResidentCareLevelHistoryService historyService;



    // ============================
    // API 23
    // GET /residents/{resident_id}/care-level-history
    // ============================
    @GetMapping("/residents/{resident_id}/care-level-history")
    public ResponseEntity<List<ResidentCareLevelHistoryResponse>> 
    getResidentCareLevelHistory(
            @PathVariable("resident_id") Long residentId
    ) {


        return ResponseEntity.ok(
                historyService.getResidentCareLevelHistory(
                        residentId
                )
        );
    }




    // ============================
    // API 24
    // POST /residents/{resident_id}/care-level-history
    // ============================
    @PostMapping("/residents/{resident_id}/care-level-history")
    public ResponseEntity<TransitionResidentCareLevelResponse>
    transitionResidentCareLevel(
            @PathVariable("resident_id") Long residentId,
            @RequestBody TransitionResidentCareLevelRequest request
    ) {


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        historyService.transitionResidentCareLevel(
                                residentId,
                                request
                        )
                );
    }





    // ============================
    // API 25
    // GET /care-level-history/active-summary
    // ============================
    @GetMapping("/care-level-history/active-summary")
    public ResponseEntity<List<ActiveCareLevelSummaryResponse>>
    getCareLevelActiveSummary() {


        return ResponseEntity.ok(
                historyService.getCareLevelActiveSummary()
        );
    }





    // ============================
    // API 26
    // PATCH /care-level-history/{id}
    // ============================
    @PatchMapping("/care-level-history/{id}")
    public ResponseEntity<ResidentCareLevelHistoryResponse>
    updateCareLevelHistory(
            @PathVariable Long id,
            @RequestBody UpdateResidentCareLevelHistoryRequest request
    ) {


        return ResponseEntity.ok(
                historyService.updateCareLevelHistory(
                        id,
                        request
                )
        );
    }





   // ============================
// API 27
// DELETE /care-level-history/{id}
// ============================
@DeleteMapping("/care-level-history/{id}")
public ResponseEntity<?> deleteCareLevelHistory(
        @PathVariable Long id
) {

    historyService.deleteCareLevelHistory(id);

    return ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(
                java.util.Map.of(
                    "message",
                    "Care level history cannot be deleted because historical records must be retained."
                )
            );
}
}