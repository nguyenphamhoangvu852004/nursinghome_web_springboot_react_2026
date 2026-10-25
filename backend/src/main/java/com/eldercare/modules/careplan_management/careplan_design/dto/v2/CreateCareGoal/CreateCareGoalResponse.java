package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CreateCareGoalResponse(
        Long id, Long carePlanVersionId, String problemOrNeed, String title,
        String desiredOutcome, String baselineValue, String targetValue, String targetUnit,
        String measurementMethod, LocalDate targetDate, String priority, String status,
        String residentPreference, OffsetDateTime createdAt, Long createdBy,
        OffsetDateTime updatedAt, Long updatedBy
) {
    public static CreateCareGoalResponse from(CareGoal goal, LocalDate targetDate) {
        return new CreateCareGoalResponse(goal.getId(), goal.getCarePlanVersionId(),
                goal.getProblemOrNeed(), goal.getTitle(), goal.getDesiredOutcome(), goal.getBaselineValue(),
                goal.getTargetValue(), goal.getTargetUnit(), goal.getMeasurementMethod(), targetDate,
                goal.getPriority(), goal.getStatus(), goal.getResidentPreference(), goal.getCreatedAt(),
                goal.getCreatedBy(), goal.getUpdatedAt(), goal.getUpdatedBy());
    }
}
