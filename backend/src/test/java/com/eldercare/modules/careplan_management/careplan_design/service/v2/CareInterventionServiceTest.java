package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention.CreateCareInterventionRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareInterventionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareInterventionServiceTest {
    @Mock private CareInterventionRepository repository;
    @Mock private CareGoalRepository goalRepository;
    @Mock private CarePlanVersionService versionService;
    private CareInterventionService service;

    @BeforeEach
    void setUp() {
        service = new CareInterventionService(repository, goalRepository, versionService);
    }

    @Test
    void createAppliesDefaultsAndAuditFields() {
        stubVersion("DRAFT");
        when(repository.save(any(CareIntervention.class))).thenAnswer(call -> call.getArgument(0));
        CareIntervention intervention = service.createIntervention(request("Assist transfer", null, null, null));
        assertEquals(1L, intervention.getCareGoalId());
        assertEquals("Assist transfer", intervention.getTitle());
        assertEquals("Use two-person assist", intervention.getInstructions());
        assertTrue(intervention.isTaskGenerationRequired());
        assertEquals(0, intervention.getSortOrder());
        assertEquals(20L, intervention.getCreatedBy());
        assertEquals(20L, intervention.getUpdatedBy());
        assertNotNull(intervention.getCreatedAt());
    }

    @Test
    void createPreservesExplicitFalseTaskGeneration() {
        stubVersion("DRAFT");
        when(repository.save(any(CareIntervention.class))).thenAnswer(call -> call.getArgument(0));
        assertFalse(service.createIntervention(request("Assist transfer", null, null, false)).isTaskGenerationRequired());
    }

    @Test
    void createRejectsInvalidInputBeforeDatabaseAccess() {
        assertThrows(IllegalArgumentException.class,
                () -> service.createIntervention(request(" ", null, null, null)));
        assertThrows(IllegalArgumentException.class,
                () -> service.createIntervention(request("Assist transfer", new Date(2000), new Date(1000), null)));
        verifyNoInteractions(repository, goalRepository, versionService);
    }

    @Test
    void createRejectsMissingGoal() {
        when(goalRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.createIntervention(request("Assist transfer", null, null, null)));
        verifyNoInteractions(repository);
    }

    @Test
    void createRejectsEffectiveVersion() {
        stubVersion("EFFECTIVE");
        assertThrows(IllegalStateException.class,
                () -> service.createIntervention(request("Assist transfer", null, null, null)));
        verifyNoInteractions(repository);
    }

    @Test
    void deleteRemovesInterventionFromDraftVersion() {
        stubIntervention();
        stubVersion("DRAFT");
        service.deleteIntervention(5L);
        verify(repository).delete(any(CareIntervention.class));
    }

    @Test
    void deleteRejectsMissingIntervention() {
        when(repository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deleteIntervention(5L));
        verify(repository, never()).delete(any(CareIntervention.class));
        verifyNoInteractions(goalRepository, versionService);
    }

    @Test
    void deleteRejectsPendingApprovalVersion() {
        stubIntervention();
        stubVersion("PENDING_APPROVAL");
        assertThrows(IllegalStateException.class, () -> service.deleteIntervention(5L));
        verify(repository, never()).delete(any(CareIntervention.class));
    }

    private void stubIntervention() {
        CareIntervention intervention = new CareIntervention();
        intervention.setId(5L);
        intervention.setCareGoalId(1L);
        when(repository.findById(5L)).thenReturn(Optional.of(intervention));
    }

    private void stubVersion(String status) {
        CareGoal goal = new CareGoal();
        goal.setCarePlanVersionId(10L);
        when(goalRepository.findById(1L)).thenReturn(Optional.of(goal));
        when(versionService.findById(10L))
                .thenReturn(CarePlanVersion.builder().id(10L).status(status).build());
    }

    private CreateCareInterventionRequest request(String title, Date start, Date end, Boolean tasks) {
        return new CreateCareInterventionRequest(1L, title, "Use two-person assist", "NURSING",
                "CNA", "QSHIFT", null, start, end, null, null, tasks, null, 20L);
    }
}
