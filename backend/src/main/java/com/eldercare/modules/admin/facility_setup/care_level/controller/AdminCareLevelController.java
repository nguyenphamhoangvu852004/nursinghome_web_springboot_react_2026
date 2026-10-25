package com.eldercare.modules.admin.facility_setup.care_level.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eldercare.modules.admin.facility_setup.care_level.dto.request.CreateCareLevelRateRequest;
import com.eldercare.modules.admin.facility_setup.care_level.dto.request.UpdateCareLevelRateRequest;
import com.eldercare.modules.admin.facility_setup.care_level.dto.request.UpdateCareLevelRequest;
import com.eldercare.modules.admin.facility_setup.care_level.dto.response.CareLevelRateResponse;
import com.eldercare.modules.admin.facility_setup.care_level.dto.response.CareLevelResponse;
import com.eldercare.modules.admin.facility_setup.care_level.service.CareLevelService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminCareLevelController {

    private final CareLevelService careLevelService;


    // ============================
    // API 19
    // GET /admin/care-levels
    // ============================
    @GetMapping("/care-levels")
    public ResponseEntity<List<CareLevelResponse>> getCareLevels() {

        return ResponseEntity.ok(
                careLevelService.getCareLevels()
        );
    }



    // ============================
    // API 20
    // PATCH /admin/care-levels/{careLevelId}
    // ============================
    @PatchMapping("/care-levels/{careLevelId}")
    public ResponseEntity<CareLevelResponse> updateCareLevel(
            @PathVariable Long careLevelId,
            @RequestBody UpdateCareLevelRequest request
    ) {

        return ResponseEntity.ok(
                careLevelService.updateCareLevel(
                        careLevelId,
                        request
                )
        );
    }



    // ============================
    // API 21
    // GET /admin/care-level-rates?care_level_id=4
    // ============================
    @GetMapping("/care-level-rates")
    public ResponseEntity<List<CareLevelRateResponse>> getCareLevelRates(
            @RequestParam(required = false) Long care_level_id
    ) {

        return ResponseEntity.ok(
                careLevelService.getCareLevelRates(
                        care_level_id
                )
        );
    }



    // ============================
    // API 22
    // POST /admin/care-level-rates
    // ============================
    @PostMapping("/care-level-rates")
    public ResponseEntity<CareLevelRateResponse> createCareLevelRate(
            @RequestBody CreateCareLevelRateRequest request
    ) {

        return ResponseEntity.ok(
                careLevelService.createCareLevelRate(request)
        );
    }



    // ============================
    // API 23
    // PUT /admin/care-level-rates/{rateId}
    // ============================
    @PutMapping("/care-level-rates/{rateId}")
    public ResponseEntity<CareLevelRateResponse> updateCareLevelRate(
            @PathVariable Long rateId,
            @RequestBody UpdateCareLevelRateRequest request
    ) {

        return ResponseEntity.ok(
                careLevelService.updateCareLevelRate(
                        rateId,
                        request
                )
        );
    }



    // ============================
    // API 24
    // DELETE /admin/care-level-rates/{rateId}
    // ============================
    @DeleteMapping("/care-level-rates/{rateId}")
    public ResponseEntity<Void> deleteCareLevelRate(
            @PathVariable Long rateId
    ) {

        careLevelService.deleteCareLevelRate(rateId);

        return ResponseEntity.noContent().build();
    }



    // ============================
    // API 25
    // POST /admin/care-level-rates/seed
    // ============================
    @PostMapping("/care-level-rates/seed")
    public ResponseEntity<List<CareLevelRateResponse>> seedSampleLOCRate() {

        return ResponseEntity.ok(
                careLevelService.seedSampleCareLevelRates()
        );
    }

}

