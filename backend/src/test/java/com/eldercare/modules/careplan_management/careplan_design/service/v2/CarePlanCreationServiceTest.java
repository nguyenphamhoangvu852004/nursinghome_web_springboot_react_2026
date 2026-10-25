package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanResponse;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlan;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarePlanCreationServiceTest {

    private static final Long CARE_PLAN_ID = 1001L;
    private static final Long INITIAL_VERSION_ID = 3101L;
    private static final Long RESIDENT_ID = 25L;
    private static final Long ADMISSION_ID = 802L;
    private static final Long CREATOR_ID = 72L;

    @Mock
    private CarePlanRepository carePlanRepository;

    @Mock
    private CarePlanVersionService carePlanVersionService;

    @Mock
    private CarePlanVersionRepository carePlanVersionRepository;

    private CarePlanService carePlanService;

    @BeforeEach
    void setUp() {
        carePlanService = new CarePlanService(carePlanRepository, carePlanVersionService,carePlanVersionRepository,null,null,null);
    }

    @Test
    @DisplayName("Create care plan: persist OPEN plan, create initial DRAFT version, then update current version")
    void createShouldPersistOpenPlanAndLinkInitialDraftVersion() {
        CarePlanVersion initialVersion = initialDraftVersion();
        AtomicInteger saveCount = new AtomicInteger();

        when(carePlanRepository.findAllByAdmissionId(ADMISSION_ID)).thenReturn(List.of());
        when(carePlanRepository.save(any(CarePlan.class))).thenAnswer(invocation -> {
            CarePlan carePlan = invocation.getArgument(0);
            int invocationNumber = saveCount.incrementAndGet();

            if (invocationNumber == 1) {
                assertEquals(RESIDENT_ID, carePlan.getResidentId());
                assertEquals(ADMISSION_ID, carePlan.getAdmissionId());
                assertEquals(CarePlan.CarePlanState.OPEN, carePlan.getState());
                assertNull(carePlan.getCurrentVersionId());
                assertEquals(CREATOR_ID, carePlan.getCreatedBy());
                assertEquals(CREATOR_ID, carePlan.getUpdatedBy());
                assertNotNull(carePlan.getOpenedAt());
                assertNotNull(carePlan.getCreatedAt());
                assertNotNull(carePlan.getUpdatedAt());
                assertNull(carePlan.getClosedAt());
                assertNull(carePlan.getClosedReason());
                carePlan.setId(CARE_PLAN_ID);
            } else {
                assertEquals(INITIAL_VERSION_ID, carePlan.getCurrentVersionId());
            }

            return carePlan;
        });
        when(carePlanVersionRepository.save(any(CarePlanVersion.class)))
                .thenReturn(initialVersion);

        CreateCarePlanResponse response = carePlanService.createNewCarePlan(createRequest());

        assertEquals(CARE_PLAN_ID, response.id());
        assertEquals(2, saveCount.get());
        assertEquals(CarePlanVersion.CarePlanVersionStatus.DRAFT, initialVersion.getStatus());
        InOrder inOrder = inOrder(carePlanRepository, carePlanVersionRepository);
        inOrder.verify(carePlanRepository).findAllByAdmissionId(ADMISSION_ID);
        inOrder.verify(carePlanRepository).save(any(CarePlan.class));
        inOrder.verify(carePlanVersionRepository).save(any(CarePlanVersion.class));
        inOrder.verify(carePlanRepository).save(any(CarePlan.class));
    }

    @Test
    @DisplayName("Create care plan: reject when the admission already has one OPEN plan")
    void createShouldRejectWhenAdmissionAlreadyHasOpenPlan() {
        CarePlan existingOpenPlan = carePlanWithState(CarePlan.CarePlanState.OPEN);
        when(carePlanRepository.findAllByAdmissionId(ADMISSION_ID)).thenReturn(List.of(existingOpenPlan));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carePlanService.createNewCarePlan(createRequest())
        );

        assertEquals("CarePlan already open for admission id " + ADMISSION_ID, exception.getMessage());
        verify(carePlanRepository, never()).save(any(CarePlan.class));
        verify(carePlanVersionRepository, never()).save(any(CarePlanVersion.class));
    }

    @Test
    @DisplayName("Create care plan: allow a new OPEN plan when all previous plans are closed")
    void createShouldSucceedWhenAdmissionHasOnlyClosedPlans() {
        CarePlan dischargedPlan = carePlanWithState(CarePlan.CarePlanState.CLOSED_DISCHARGED);
        CarePlan transferredPlan = carePlanWithState(CarePlan.CarePlanState.CLOSED_TRANSFERRED);
        CarePlanVersion initialVersion = initialDraftVersion();

        when(carePlanRepository.findAllByAdmissionId(ADMISSION_ID))
                .thenReturn(List.of(dischargedPlan, transferredPlan));
        when(carePlanRepository.save(any(CarePlan.class))).thenAnswer(invocation -> {
            CarePlan carePlan = invocation.getArgument(0);
            carePlan.setId(CARE_PLAN_ID);
            return carePlan;
        });
        when(carePlanVersionRepository.save(any(CarePlanVersion.class)))
                .thenReturn(initialVersion);

        CreateCarePlanResponse response = carePlanService.createNewCarePlan(createRequest());

        assertEquals(CARE_PLAN_ID, response.id());
        verify(carePlanRepository, times(2)).save(any(CarePlan.class));
        verify(carePlanVersionRepository).save(any(CarePlanVersion.class));
    }

    @Test
    @DisplayName("Create care plan: do not update current version when initial version creation fails")
    void createShouldNotUpdateCurrentVersionWhenInitialVersionCreationFails() {
        RuntimeException versionCreationFailure = new RuntimeException("Cannot create initial version");

        when(carePlanRepository.findAllByAdmissionId(ADMISSION_ID)).thenReturn(List.of());
        when(carePlanRepository.save(any(CarePlan.class))).thenAnswer(invocation -> {
            CarePlan carePlan = invocation.getArgument(0);
            carePlan.setId(CARE_PLAN_ID);
            return carePlan;
        });
        when(carePlanVersionRepository.save(any(CarePlanVersion.class)))
                .thenThrow(versionCreationFailure);

        RuntimeException actualException = assertThrows(
                RuntimeException.class,
                () -> carePlanService.createNewCarePlan(createRequest())
        );

        assertSame(versionCreationFailure, actualException);
        verify(carePlanRepository).save(any(CarePlan.class));
        verify(carePlanVersionRepository).save(any(CarePlanVersion.class));
    }

    private CreateCarePlanRequest createRequest() {
        return new CreateCarePlanRequest(
                RESIDENT_ID,
                ADMISSION_ID,
                CREATOR_ID,
                null,
                null,
                null,
                null
        );
    }

    private CarePlan carePlanWithState(String state) {
        CarePlan carePlan = new CarePlan();
        carePlan.setAdmissionId(ADMISSION_ID);
        carePlan.setState(state);
        return carePlan;
    }

    private CarePlanVersion initialDraftVersion() {
        return CarePlanVersion.builder()
                .id(INITIAL_VERSION_ID)
                .carePlanId(CARE_PLAN_ID)
                .versionNumber(1)
                .status(CarePlanVersion.CarePlanVersionStatus.DRAFT)
                .basedOnAdmissionId(ADMISSION_ID)
                .createdBy(CREATOR_ID)
                .updatedBy(CREATOR_ID)
                .build();
    }
}
