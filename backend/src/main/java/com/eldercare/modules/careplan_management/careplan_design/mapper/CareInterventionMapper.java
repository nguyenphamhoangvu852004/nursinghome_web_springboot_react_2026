package com.eldercare.modules.careplan_management.careplan_design.mapper;

import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;

public final class CareInterventionMapper {

    private CareInterventionMapper() {
    }

    public static CareInterventionEntity attachToCareGoal(CareInterventionEntity intervention,
            CareGoalEntity careGoal) {
        if (intervention == null) {
            return null;
        }
        intervention.setCareGoal(careGoal);
        return intervention;
    }
}
