package com.eldercare.modules.careplan_management.careplan_design.mapper;

import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;

public final class CareGoalMapper {

    private CareGoalMapper() {
    }

    public static CareGoalEntity attachToCarePlan(CareGoalEntity careGoal, CarePlanEntity carePlan) {
        if (careGoal == null) {
            return null;
        }
        careGoal.setCarePlan(carePlan);
        careGoal.getListCareIntervention()
                .forEach(intervention -> CareInterventionMapper.attachToCareGoal(intervention, careGoal));
        return careGoal;
    }
}
