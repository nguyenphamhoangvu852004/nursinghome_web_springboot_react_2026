package com.eldercare.modules.careplan_management.careplan_design.dto.v2.GetCarePlan;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlan;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;

import java.util.List;


public record GetCarePlanDetailByIDResponseDTO(
        CarePlan carePlan,
        List<CarePlanVersion> listCarePlanVersion
) {
}
