package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention;

import java.util.Date;

public record CreateCareInterventionRequest(
        Long careGoalId,
        String title,
        String instructions,
        String responsibleDiscipline,
        String assignedRole,
        String frequencyCode,
        String scheduleDefinition,
        Date startDate,
        Date endDate,
        String prnCriteria,
        String evidenceOrRationale,
        Boolean requiresTaskGeneration,
        Integer sortOrder,
        Long actorId
) {
}
