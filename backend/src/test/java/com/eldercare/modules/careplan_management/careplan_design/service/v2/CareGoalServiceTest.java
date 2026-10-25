package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal.CreateCareGoalRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareGoalServiceTest {
    @Mock private CareGoalRepository repository;
    @Mock private CarePlanVersionService versionService;
    private CareGoalService service;

    @BeforeEach
    void setUp() {
        service = new CareGoalService(repository, versionService);
    }

    @Test
    void createDefaultsToActiveLowPriorityAndRecordsActor() {
        when(versionService.findById(10L)).thenReturn(version("DRAFT"));
        when(repository.save(any(CareGoal.class))).thenAnswer(call -> call.getArgument(0));
        CareGoal goal = service.createGoal(request("Safe transfer"));
        assertEquals(10L, goal.getCarePlanVersionId());
        assertEquals("Safe transfer", goal.getTitle());
        assertEquals("LOW", goal.getPriority());
        assertEquals("ACTIVE", goal.getStatus());
        assertEquals(20L, goal.getCreatedBy());
        assertEquals(20L, goal.getUpdatedBy());
        assertNotNull(goal.getCreatedAt());
    }

    @Test
    void createRejectsBlankTitleWithoutSaving() {
        assertThrows(IllegalArgumentException.class, () -> service.createGoal(request(" ")));
        verifyNoInteractions(repository, versionService);
    }

    @Test
    void createRejectsEffectiveVersion() {
        when(versionService.findById(10L)).thenReturn(version("EFFECTIVE"));
        assertThrows(IllegalStateException.class, () -> service.createGoal(request("Safe transfer")));
        verifyNoInteractions(repository);
    }

    @Test
    void deleteRejectsMissingGoal() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deleteGoal(1L));
        verify(repository, never()).delete(any(CareGoal.class));
    }

    @Test
    void deleteRemovesGoalFromDraftVersion() {
        CareGoal goal = new CareGoal();
        goal.setCarePlanVersionId(10L);
        when(repository.findById(1L)).thenReturn(Optional.of(goal));
        when(versionService.findById(10L)).thenReturn(version("DRAFT"));
        service.deleteGoal(1L);
        verify(repository).delete(goal);
    }

    @Test
    void deleteRejectsPendingApprovalVersion() {
        CareGoal goal = new CareGoal();
        goal.setCarePlanVersionId(10L);
        when(repository.findById(1L)).thenReturn(Optional.of(goal));
        when(versionService.findById(10L)).thenReturn(version("PENDING_APPROVAL"));
        assertThrows(IllegalStateException.class, () -> service.deleteGoal(1L));
        verify(repository, never()).delete(any(CareGoal.class));
    }

    private CarePlanVersion version(String status) {
        return CarePlanVersion.builder().id(10L).status(status).build();
    }

    private CreateCareGoalRequest request(String title) {
        return new CreateCareGoalRequest(10L, "Fall risk", title, "No falls", "Two-person assist",
                null, null, null, null, null, null, 20L);
    }
}
