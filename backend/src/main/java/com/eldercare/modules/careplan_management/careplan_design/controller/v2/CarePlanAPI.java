package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.*;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion.CreateCarePlanVersionRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion.CreateCarePlanVersionRespone;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

public interface CarePlanAPI {
    ApiResponse<CreateCarePlanResponse> createCarePlan(@RequestBody CreateCarePlanRequest carePlan);
    ApiResponse<CreateCarePlanVersionRespone> createNewVersion(@RequestBody CreateCarePlanVersionRequest version);
    ApiResponse<CarePlanVersion> changeCarePlanVersionStatus(@RequestBody HashMap<String,String> body);
}
