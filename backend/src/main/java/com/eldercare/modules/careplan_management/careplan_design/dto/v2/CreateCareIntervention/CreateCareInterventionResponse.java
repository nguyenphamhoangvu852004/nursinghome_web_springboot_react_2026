package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public record CreateCareInterventionResponse(
        Long id, Long careGoalId, String title, String instructions, String responsibleDiscipline,
        String assignedRole, String frequencyCode, String scheduleDefinition,
        LocalDate startDate, LocalDate endDate, String prnCriteria, String evidenceOrRationale,
        boolean requiresTaskGeneration, int sortOrder, OffsetDateTime createdAt, Long createdBy,
        OffsetDateTime updatedAt, Long updatedBy
) {
    public static CreateCareInterventionResponse from(CareIntervention value) {
        return new CreateCareInterventionResponse(value.getId(), value.getCareGoalId(), value.getTitle(),
                value.getInstructions(), value.getResponsibleDiscipline(), value.getAssignedRole(),
                value.getFrequencyCode(), value.getScheduleDefinition(), dateOnly(value.getStartDate()),
                dateOnly(value.getEndDate()), value.getPrnCriteria(), value.getEvidenceOrRationale(),
                value.isTaskGenerationRequired(), value.getSortOrder(), value.getCreatedAt(),
                value.getCreatedBy(), value.getUpdatedAt(), value.getUpdatedBy());
    }

    private static LocalDate dateOnly(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
