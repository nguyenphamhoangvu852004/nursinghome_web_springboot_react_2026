package com.eldercare.modules.human_resources.staffing_compliance.staffing.service;

import com.eldercare.modules.human_resources.staffing_compliance.staffing.dto.request.UpdateStaffingRatioRequest;
import com.eldercare.modules.human_resources.staffing_compliance.staffing.dto.response.StaffingRatioResponse;

public interface StaffingRatioService {

    StaffingRatioResponse getStaffingRatio();

    StaffingRatioResponse updateStaffingRatio(UpdateStaffingRatioRequest request);
}
