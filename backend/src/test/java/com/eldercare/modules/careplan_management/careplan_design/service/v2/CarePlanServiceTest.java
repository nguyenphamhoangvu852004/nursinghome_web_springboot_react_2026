package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanResponse;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlan;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanVersionRepository;
import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class CarePlanServiceTest {
    //services
    private CarePlanService carePlanService;
    private CarePlanVersionService carePlanVersionService;

    //repositories
    @Mock
    private CarePlanRepository carePlanRepository;
    @Mock
    private CarePlanVersionRepository carePlanVersionRepository;

    @BeforeEach
    void setUp() {
        carePlanVersionService = new CarePlanVersionService(carePlanVersionRepository);
        carePlanService = new CarePlanService(carePlanRepository, carePlanVersionService,null,null,null,null);
    }

    @Test
    @Disabled("Superseded by CarePlanCreationServiceTest")
    @DisplayName(value = "createdAt and updatedAt must be init after create new Care Plan")
    void create() {
        Long residentId = (long) 12;
        Long admissionId = (long) 23;
        Long currentVersionId = (long) 55;
        Long creatorId = (long) 984;

        CreateCarePlanResponse responseDTO = carePlanService.createNewCarePlan(new CreateCarePlanRequest(
                residentId, admissionId, creatorId,null,null,null,null
        ));
        assertNotNull(responseDTO);
    }

    @Test
    @Disabled("Superseded by CarePlanCreationServiceTest")
    @DisplayName(value = "Cap nhat thong tin moi cua Care Plan")
    void update() {
        OffsetDateTime now = OffsetDateTime.now();
        CarePlan carePlan = new CarePlan();
        carePlan.setId((long) 1);
        carePlan.setResidentId((long) 32);
        carePlan.setAdmissionId((long) 34);
        carePlan.setCurrentVersionId((long) 55);
        carePlan.setState(CarePlan.CarePlanState.OPEN);
        carePlan.setOpenedAt(now);

        CarePlan newCarePlan = new CarePlan();
        newCarePlan.setAdmissionId((long) 34);
        carePlan.update(newCarePlan);

        System.out.println(carePlan.toString());
        assertEquals(newCarePlan.getAdmissionId(), (long) 34);
    }

    @Test
    @Disabled("Superseded by CarePlanCreationServiceTest")
    @DisplayName(value = "Khi tạo mới 1 Care Plan, phải tạo mới 1 Care Plan Version với trạng tháì DRAFT")
    void createCarePlanWithCarePlanVersionCaredFollowedByCarePlan() {

        //prepare
        Long residentId = (long) 4;
        Long admissionId = (long) 23;
        Long currentVersionId = (long) 55;
        Long creatorId = (long) 984;

        OffsetDateTime now = OffsetDateTime.now();

        OffsetDateTime dateCreateAdmission = now.minusHours(2);
        // input
        CreateCarePlanRequest input = new CreateCarePlanRequest(
                residentId, admissionId, creatorId,null,null,null,null
        );

        // output
        AdmissionEntity admissionEntity = AdmissionEntity.builder()
                .id((long)4)
                .admissionDate(dateCreateAdmission.toLocalDate())
                .facility(null)
                .isCurrent(true)
                .preAdmissionScreening(null)
                .resident(null)
                .createdAt(now)
                .dischargeDate(null)
                .dischargeReason(null)
                .build();
        CarePlan carePlan = CarePlan.builder()
                .id((long) 1)
                .admissionId(admissionId)
                .currentVersionId(null)
                .createdAt(now)
                .createdBy(creatorId)
                .updatedAt(now)
                .updatedBy(creatorId)
                .build();
        CarePlanVersion carePlanVersion = CarePlanVersion.builder()
                .id((long) 1)
                .carePlanId(carePlan.getId())
                .status(CarePlanVersion.CarePlanVersionStatus.DRAFT)
                .basedOnAdmissionId(null)
                .basedOnVersionId(null)
                .basedOnAdmissionId(admissionEntity.getId())
                .createdAt(now)
                .createdBy(creatorId)
                .updatedAt(now)
                .updatedBy(creatorId)
                .build();
        carePlan.setCurrentVersionId(carePlanVersion.getId());



        //logic


        when(this.carePlanRepository.save(any(CarePlan.class))).thenReturn(carePlan);
        when(this.carePlanVersionRepository.save(any(CarePlanVersion.class))).thenReturn(carePlanVersion);

        CreateCarePlanResponse carePlanCreated = this.carePlanService.createNewCarePlan(input);


        assertEquals(carePlanCreated.id(), carePlan.getId());
//        assertEquals(carePlanVersion.getId(),);

    }

    @Test
    @DisplayName(value = "tao moi care_plan, tra ve care plan voi state la open va version la draft vua insert vao db")
    void createDraftCarePlan(){
        Long residentId = 12L;
        Long admissionId = 23L;
        Long creatorId = 984L;
        Long carePlanId = 100L;
        Long versionId = 200L;
        CreateCarePlanRequest input = new CreateCarePlanRequest(
                residentId, admissionId, creatorId, null, null, null, null
        );
        CreateCarePlanResponse expectedOutput = new CreateCarePlanResponse(carePlanId);

        when(carePlanRepository.findAllByAdmissionId(admissionId)).thenReturn(List.of());
        AtomicInteger saveCount = new AtomicInteger();
        when(carePlanRepository.save(any(CarePlan.class))).thenAnswer(invocation -> {
            CarePlan plan = invocation.getArgument(0);
            assertEquals(residentId, plan.getResidentId());
            assertEquals(admissionId, plan.getAdmissionId());
            assertEquals(creatorId, plan.getCreatedBy());
            assertEquals(creatorId, plan.getUpdatedBy());
            assertEquals(CarePlan.CarePlanState.OPEN, plan.getState());
            if (saveCount.incrementAndGet() == 1) {
                assertNull(plan.getId());
                assertNull(plan.getCurrentVersionId());
                plan.setId(carePlanId);
            } else {
                assertEquals(carePlanId, plan.getId());
                assertEquals(versionId, plan.getCurrentVersionId());
            }
            return plan;
        });
        when(carePlanVersionRepository.save(any(CarePlanVersion.class))).thenAnswer(invocation -> {
            CarePlanVersion version = invocation.getArgument(0);
            assertNull(version.getId());
            assertEquals(carePlanId, version.getCarePlanId());
            assertEquals(admissionId, version.getBasedOnAdmissionId());
            assertEquals(CarePlanVersion.CarePlanVersionStatus.DRAFT, version.getStatus());
            assertEquals(creatorId, version.getCreatedBy());
            assertEquals(creatorId, version.getUpdatedBy());
            version.setId(versionId);
            return version;
        });

        CreateCarePlanResponse actualOutput = carePlanService.createNewCarePlan(input);

        assertEquals(expectedOutput, actualOutput);
        assertEquals(2, saveCount.get());
        var order = inOrder(carePlanRepository, carePlanVersionRepository);
        order.verify(carePlanRepository).findAllByAdmissionId(admissionId);
        order.verify(carePlanRepository).save(any(CarePlan.class));
        order.verify(carePlanVersionRepository).save(any(CarePlanVersion.class));
        order.verify(carePlanRepository).save(any(CarePlan.class));
        order.verifyNoMoreInteractions();
    }



}
