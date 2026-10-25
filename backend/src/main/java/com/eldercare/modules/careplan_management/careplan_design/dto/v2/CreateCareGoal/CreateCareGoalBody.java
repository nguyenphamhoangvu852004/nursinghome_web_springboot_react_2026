package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal;

import java.time.LocalDate;

/** Public input: version and actor are supplied by the server. */
public record CreateCareGoalBody(
        String problemOrNeed, String title, String desiredOutcome, String baselineValue,
        String targetValue, String targetUnit, String measurementMethod, LocalDate targetDate,
        String priority, String residentPreference
) {}
