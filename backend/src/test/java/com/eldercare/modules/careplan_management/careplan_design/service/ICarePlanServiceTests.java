package com.eldercare.modules.careplan_management.careplan_design.service;

import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaAssessmentRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaCarePlanRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import java.util.Optional;

import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ICarePlanServiceTests {
    @Mock
    private JpaCarePlanRepository carePlanRepository;

    @Mock
    private ResidentRepository residentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JpaAssessmentRepository assessmentRepository;

    @InjectMocks
    private CarePlanServiceImpl carePlanService;

    @Test
    void myFirstTest() {
        int x = 5;
        int y = 9;
        assertEquals(14, x + y);
    }

    @Test
    void createCarePlan_withPurposeDraftProvidedneedToSetStatusIntoDraft() {
        // INPUT
        CreateCarePlanRequestDTO input = new CreateCarePlanRequestDTO();
        input.residentId = 1;
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention1 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Measure Blood Pressure",
                        "NURSE"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention2 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Assist Walking",
                        "CAREGIVER"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO goal =
                new CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO(
                        "Maintain Stable Blood Pressure",
                        "Resident should maintain normal blood pressure.",
                        List.of(intervention1, intervention2)
                );
        input.listCareGoal = List.of(goal);
        String purpose = "DRAFT";

        // Mock Repository behaviorors
        ResidentEntity residentEntity = new ResidentEntity();
        residentEntity.setId(1L);
        residentEntity.setChartLocked(false);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(residentEntity));
        CarePlanEntity savedEntity = new CarePlanEntity();
        savedEntity.setId(1);
        when(carePlanRepository.save(any(CarePlanEntity.class)))
                .thenReturn(savedEntity);

        // service calling
        CreateCarePlanResponseDTO output = this.carePlanService.createCarePlan(input, purpose);

        // OUTPUT expected
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.id);

        ArgumentCaptor<CarePlanEntity> captor =
                ArgumentCaptor.forClass(CarePlanEntity.class);

        verify(carePlanRepository)
                .save(captor.capture());

        CarePlanEntity saved = captor.getValue();

        assertEquals(
                CarePlanStatusEnum.DRAFT,
                saved.getStatus()
        );

    }

    @Test
    void createCarePlan_withPurposeNeedReviewProvided_needToSetStatusIntoPendingReview() {
        // INPUT
        CreateCarePlanRequestDTO input = new CreateCarePlanRequestDTO();
        input.residentId = 1;
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention1 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Measure Blood Pressure",
                        "NURSE"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention2 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Assist Walking",
                        "CAREGIVER"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO goal =
                new CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO(
                        "Maintain Stable Blood Pressure",
                        "Resident should maintain normal blood pressure.",
                        List.of(intervention1, intervention2)
                );
        input.listCareGoal = List.of(goal);
        String purpose = "NEED_REVIEW";

        // Mock Repository behaviorors
        ResidentEntity residentEntity = new ResidentEntity();
        residentEntity.setId(1L);
        residentEntity.setChartLocked(false);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(residentEntity));
        CarePlanEntity savedEntity = new CarePlanEntity();
        savedEntity.setId(1);
        when(carePlanRepository.save(any(CarePlanEntity.class)))
                .thenReturn(savedEntity);

        // service calling
        CreateCarePlanResponseDTO output = this.carePlanService.createCarePlan(input, purpose);

        // OUTPUT expected
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.id);

        ArgumentCaptor<CarePlanEntity> captor =
                ArgumentCaptor.forClass(CarePlanEntity.class);

        verify(carePlanRepository)
                .save(captor.capture());

        CarePlanEntity saved = captor.getValue();

        assertEquals(
                CarePlanStatusEnum.PENDING_REVIEW,
                saved.getStatus()
        );

    }

    @Test
    void createCarePlan_withNoPurposeProvided_needToSetDefaultStatusIntoDraft() {
        CreateCarePlanRequestDTO input = new CreateCarePlanRequestDTO();
        input.residentId = 1;
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention1 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Measure Blood Pressure",
                        "NURSE"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention2 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Assist Walking",
                        "CAREGIVER"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO goal =
                new CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO(
                        "Maintain Stable Blood Pressure",
                        "Resident should maintain normal blood pressure.",
                        List.of(intervention1, intervention2)
                );
        input.listCareGoal = List.of(goal);
        String purpose = null;

        // Mock Repository behaviorors
        ResidentEntity residentEntity = new ResidentEntity();
        residentEntity.setId(1L);
        residentEntity.setChartLocked(false);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(residentEntity));
        CarePlanEntity savedEntity = new CarePlanEntity();
        savedEntity.setId(1);
        when(carePlanRepository.save(any(CarePlanEntity.class)))
                .thenReturn(savedEntity);

        // service calling
        CreateCarePlanResponseDTO output = this.carePlanService.createCarePlan(input, null);

        // OUTPUT expected
        ArgumentCaptor<CarePlanEntity> captor =
                ArgumentCaptor.forClass(CarePlanEntity.class);

        verify(carePlanRepository)
                .save(captor.capture());

        CarePlanEntity saved = captor.getValue();

        assertEquals(
                CarePlanStatusEnum.DRAFT,
                saved.getStatus()
        );
    }

    @Test
    void createCarePlan_withResidentHasCharLocked_needToThrowRuntimeException() {

        //INPUT
        CreateCarePlanRequestDTO input = new CreateCarePlanRequestDTO();
        input.residentId = 1;
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention1 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Measure Blood Pressure",
                        "NURSE"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention2 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Assist Walking",
                        "CAREGIVER"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO goal =
                new CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO(
                        "Maintain Stable Blood Pressure",
                        "Resident should maintain normal blood pressure.",
                        List.of(intervention1, intervention2)
                );
        input.listCareGoal = List.of(goal);
        String purpose = "DRAFT";

        // Mock Repository behaviorors
        ResidentEntity lockedResident = new ResidentEntity();
        lockedResident.setId(1L);
        // mock this resident chart lock = true
        lockedResident.setChartLocked(true);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(lockedResident));

        // OUTPUT expected
        RuntimeException exception = assertThrows(RuntimeException.class,
                // calling service
                () -> carePlanService.createCarePlan(input, "DRAFT")
        );

        assertEquals("Resident is locked, can not create care plan for this resident",
                exception.getMessage());

        verify(residentRepository).findById(1L);
        verify(carePlanRepository, never()).save(any(CarePlanEntity.class));
    }

    @Test
    void createCarePlan_withNoResidentFound_needToThrowRuntimeException() {

        //INPUT
        CreateCarePlanRequestDTO input = new CreateCarePlanRequestDTO();
        input.residentId = 1;
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention1 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Measure Blood Pressure",
                        "NURSE"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO intervention2 =
                new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                        "Assist Walking",
                        "CAREGIVER"
                );
        CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO goal =
                new CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO(
                        "Maintain Stable Blood Pressure",
                        "Resident should maintain normal blood pressure.",
                        List.of(intervention1, intervention2)
                );
        input.listCareGoal = List.of(goal);
        String purpose = "DRAFT";

        // Mock Repository behaviorors
        ResidentEntity lockedResident = new ResidentEntity();
        lockedResident.setId(1L);
        // mock this resident chart lock = true
        lockedResident.setChartLocked(true);
        when(residentRepository.findById(1L)).thenReturn(Optional.of(lockedResident));

        // OUTPUT expected
        RuntimeException exception = assertThrows(RuntimeException.class,
                // calling service
                () -> carePlanService.createCarePlan(input, "DRAFT")
        );

        assertEquals("Resident is locked, can not create care plan for this resident",
                exception.getMessage());

        verify(residentRepository).findById(1L);
        verify(carePlanRepository, never()).save(any(CarePlanEntity.class));
    }

    @Test
    void createCarePlan_withListCareGoalEqualToNull_needToThrowRuntimeException() {

        //INPUT
        CreateCarePlanRequestDTO input = new CreateCarePlanRequestDTO();
        input.residentId = 1;
        input.listCareGoal = null;

        // OUTPUT expected
        RuntimeException exception = assertThrows(RuntimeException.class,
                // calling service
                () -> carePlanService.createCarePlan(input, null)
        );
        assertEquals("Care plan need at least one care goal", exception.getMessage());

    }

}
