package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention.CreateCareInterventionRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareInterventionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CareInterventionService {
    private final CareInterventionRepository careInterventionRepository;
    private final CareGoalRepository careGoalRepository;
    private final CarePlanVersionService carePlanVersionService;

    public CareInterventionService(CareInterventionRepository careInterventionRepository,
                                   CareGoalRepository careGoalRepository,
                                   CarePlanVersionService carePlanVersionService) {
        this.careInterventionRepository = careInterventionRepository;
        this.careGoalRepository = careGoalRepository;
        this.carePlanVersionService = carePlanVersionService;
    }

    @Transactional
    public CareIntervention createIntervention(CreateCareInterventionRequest request) {
        Objects.requireNonNull(request, "Request is required");
        Objects.requireNonNull(request.careGoalId(), "Care goal id is required");
        Objects.requireNonNull(request.actorId(), "Actor id is required");
        validateText(request.title(), "Title", 255, true);
        validateText(request.instructions(), "Instructions", 2000, true);
        validateText(request.responsibleDiscipline(), "Responsible discipline", 50, false);
        validateText(request.assignedRole(), "Assigned role", 50, true);
        validateText(request.frequencyCode(), "Frequency code", 30, true);
        validateText(request.scheduleDefinition(), "Schedule definition", 1000, false);
        validateText(request.prnCriteria(), "PRN criteria", 1000, false);
        validateText(request.evidenceOrRationale(), "Evidence or rationale", 1000, false);
        if (request.startDate() != null && request.endDate() != null
                && request.endDate().before(request.startDate())) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
        checkDraftVersionOfGoal(request.careGoalId());

        CareIntervention intervention = new CareIntervention();
        intervention.setCareGoalId(request.careGoalId());
        intervention.setTitle(request.title());
        intervention.setInstructions(request.instructions());
        intervention.setResponsibleDiscipline(request.responsibleDiscipline());
        intervention.setAssignedRole(request.assignedRole());
        intervention.setFrequencyCode(request.frequencyCode());
        intervention.setScheduleDefinition(request.scheduleDefinition());
        intervention.setStartDate(request.startDate());
        intervention.setEndDate(request.endDate());
        intervention.setPrnCriteria(request.prnCriteria());
        intervention.setEvidenceOrRationale(request.evidenceOrRationale());
        intervention.setTaskGenerationRequired(request.requiresTaskGeneration() == null
                || request.requiresTaskGeneration());
        intervention.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        intervention.setCreatedBy(request.actorId());
        intervention.setUpdatedBy(request.actorId());
        OffsetDateTime now = OffsetDateTime.now();
        intervention.setCreatedAt(now);
        intervention.setUpdatedAt(now);
        return careInterventionRepository.save(intervention);
    }

    @Transactional
    public void deleteIntervention(Long interventionId) {
        Objects.requireNonNull(interventionId, "Intervention id is required");
        CareIntervention intervention = careInterventionRepository.findById(interventionId)
                .orElseThrow(() -> new NotFoundException("Care Intervention not found: " + interventionId));
        checkDraftVersionOfGoal(intervention.getCareGoalId());
        careInterventionRepository.delete(intervention);
    }

    private void checkDraftVersionOfGoal(Long goalId) {
        CareGoal goal = careGoalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Care Goal not found: " + goalId));
        CarePlanVersion version = carePlanVersionService.findById(goal.getCarePlanVersionId());
        if (!CarePlanVersion.CarePlanVersionStatus.DRAFT.equals(version.getStatus())) {
            throw new IllegalStateException("Interventions can only be changed in a DRAFT care plan version: "
                    + version.getId());
        }
    }

    private void validateText(String value, String field, int maxLength, boolean required) {
        if (required && (value == null || value.isBlank())) {
            throw new IllegalArgumentException(field + " is required");
        }
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must not exceed " + maxLength + " characters");
        }
    }
}
