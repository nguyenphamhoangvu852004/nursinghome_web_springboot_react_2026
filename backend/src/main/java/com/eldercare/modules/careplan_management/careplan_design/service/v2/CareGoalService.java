package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal.CreateCareGoalRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareGoalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class CareGoalService {
    private final CareGoalRepository careGoalRepository;
    private final CarePlanVersionService carePlanVersionService;

    public CareGoalService(CareGoalRepository careGoalRepository, CarePlanVersionService carePlanVersionService) {
        this.careGoalRepository = careGoalRepository;
        this.carePlanVersionService = carePlanVersionService;
    }

    @Transactional
    public CareGoal createGoal(CreateCareGoalRequest request) {
        Objects.requireNonNull(request, "Request is required");
        Objects.requireNonNull(request.carePlanVersionId(), "Care plan version id is required");
        Objects.requireNonNull(request.actorId(), "Actor id is required");
        validateText(request.problemOrNeed(), "Problem or need", 1000, true);
        validateText(request.title(), "Title", 255, true);
        validateText(request.desiredOutcome(), "Desired outcome", 1000, true);
        validateText(request.baselineValue(), "Baseline value", 2000, true);
        validateText(request.targetValue(), "Target value", 255, false);
        validateText(request.targetUnit(), "Target unit", 50, false);
        validateText(request.measurementMethod(), "Measurement method", 500, false);
        validateText(request.residentPreference(), "Resident preference", 1000, false);
        String priority = request.priority() == null ? CareGoal.CareGoalPriority.LOW : request.priority();
        if (!CareGoal.CareGoalPriority.LOW.equals(priority)
                && !CareGoal.CareGoalPriority.MEDIUM.equals(priority)
                && !CareGoal.CareGoalPriority.HIGH.equals(priority)) {
            throw new IllegalArgumentException("Priority must be LOW, MEDIUM or HIGH");
        }
        checkDraftVersion(request.carePlanVersionId());

        CareGoal goal = new CareGoal();
        goal.setCarePlanVersionId(request.carePlanVersionId());
        goal.setProblemOrNeed(request.problemOrNeed());
        goal.setTitle(request.title());
        goal.setDesiredOutcome(request.desiredOutcome());
        goal.setBaselineValue(request.baselineValue());
        goal.setTargetValue(request.targetValue());
        goal.setTargetUnit(request.targetUnit());
        goal.setMeasurementMethod(request.measurementMethod());
        goal.setTargetDate(request.targetDate());
        goal.setPriority(priority);
        goal.setStatus(CareGoal.CareGoalStatus.ACTIVE);
        goal.setResidentPreference(request.residentPreference());
        goal.setCreatedBy(request.actorId());
        goal.setUpdatedBy(request.actorId());
        OffsetDateTime now = OffsetDateTime.now();
        goal.setCreatedAt(now);
        goal.setUpdatedAt(now);
        return careGoalRepository.save(goal);
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        Objects.requireNonNull(goalId, "Goal id is required");
        CareGoal goal = careGoalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Care Goal not found: " + goalId));
        checkDraftVersion(goal.getCarePlanVersionId());
        careGoalRepository.delete(goal);
    }

    private void checkDraftVersion(Long versionId) {
        CarePlanVersion version = carePlanVersionService.findById(versionId);
        if (!CarePlanVersion.CarePlanVersionStatus.DRAFT.equals(version.getStatus())) {
            throw new IllegalStateException("Goals can only be changed in a DRAFT care plan version: " + versionId);
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
