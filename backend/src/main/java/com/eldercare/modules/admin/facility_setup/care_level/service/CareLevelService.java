package com.eldercare.modules.admin.facility_setup.care_level.service;

import java.util.List;

import com.eldercare.modules.admin.facility_setup.care_level.dto.request.CreateCareLevelRateRequest;
import com.eldercare.modules.admin.facility_setup.care_level.dto.request.UpdateCareLevelRateRequest;
import com.eldercare.modules.admin.facility_setup.care_level.dto.request.UpdateCareLevelRequest;
import com.eldercare.modules.admin.facility_setup.care_level.dto.response.CareLevelRateResponse;
import com.eldercare.modules.admin.facility_setup.care_level.dto.response.CareLevelResponse;

public interface CareLevelService {

    // API 19
    List<CareLevelResponse> getCareLevels();

    // API 20
    CareLevelResponse updateCareLevel(Long careLevelId, UpdateCareLevelRequest request);

    // API 21
    List<CareLevelRateResponse> getCareLevelRates(Long careLevelId);

    // API 22
    CareLevelRateResponse createCareLevelRate(CreateCareLevelRateRequest request);

    // API 23
    CareLevelRateResponse updateCareLevelRate(Long rateId, UpdateCareLevelRateRequest request);

    // API 24
    void deleteCareLevelRate(Long rateId);

    // API 25
    List<CareLevelRateResponse> seedSampleCareLevelRates();
 
}

