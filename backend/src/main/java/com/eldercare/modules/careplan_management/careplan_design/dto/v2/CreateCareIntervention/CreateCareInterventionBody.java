package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention;

import java.time.LocalDate;

/** Goal comes from the URL; actor comes from the authenticated session. */
public record CreateCareInterventionBody(
        String title, String instructions, String responsibleDiscipline, String assignedRole,
        String frequencyCode, String scheduleDefinition, LocalDate startDate, LocalDate endDate,
        String prnCriteria, String evidenceOrRationale, Boolean requiresTaskGeneration, Integer sortOrder
) {}
