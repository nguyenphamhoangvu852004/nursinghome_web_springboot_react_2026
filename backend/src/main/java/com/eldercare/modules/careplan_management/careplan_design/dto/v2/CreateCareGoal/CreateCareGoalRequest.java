package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal;

import java.util.Date;

public record CreateCareGoalRequest(
        Long carePlanVersionId,
        String problemOrNeed,
        String title,
        String desiredOutcome,
        String baselineValue,
        String targetValue,
        String targetUnit,
        String measurementMethod,
        Date targetDate,
        String priority,
        String residentPreference,
        Long actorId
) {
}
