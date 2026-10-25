package com.eldercare.modules.careplan_management.careplan_design.service.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion.CreateCarePlanVersionRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.GetCarePlan.GetCarePlanDetailByIDResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.RejectCarePlanVersion.RejectCarePlanVersionRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.SubmitWaitingForReview.SubmitWaitingForReviewRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlan;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanVersionRepository;
import com.eldercare.modules.resident_intake.admission_ledger.repository.AdmissionRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;


@Service
public class CarePlanService {

    private final CarePlanRepository carePlanRepository;
    private final CarePlanVersionService carePlanVersionService;
    private final CarePlanVersionRepository carePlanVersionRepository;
    private final ResidentRepository residentRepository;
    private final AdmissionRepository admissionRepository;
    private final CareInterventionService careInterventionService;

    public CarePlanService(CarePlanRepository carePlanRepository, CarePlanVersionService carePlanVersionService, CarePlanVersionRepository carePlanVersionRepository, ResidentRepository residentRepository, AdmissionRepository admissionRepository, CareInterventionService careInterventionService) {
        this.carePlanRepository = carePlanRepository;
        this.carePlanVersionService = carePlanVersionService;
        this.carePlanVersionRepository = carePlanVersionRepository;
        this.residentRepository = residentRepository;
        this.admissionRepository = admissionRepository;
        this.careInterventionService = careInterventionService;
    }

     @Transactional
    public CreateCarePlanResponse createNewCarePlan(CreateCarePlanRequest inputData) {
        // toi da 1 care plan dang OPEN
        this.checkOneCarePlanOpenForAdmission(inputData.admissionId());

        // luu care plan db
        CarePlan carePlan = CarePlan.create(inputData.residentId(), inputData.admissionId(), inputData.creatorId());
        carePlan = this.carePlanRepository.save(carePlan);

        CarePlanVersion carePlanVersion = this.carePlanVersionRepository.save(
                carePlan.prepareInitialVersionForPersistence());

        // update lai version trong care plan
        carePlan.linkPersistedCurrentVersion(carePlanVersion);
        this.carePlanRepository.save(carePlan);

        return new CreateCarePlanResponse(carePlan.getId());
    }

    public Object update(Long carePlanId, CarePlan newCarePlanInfo) {
        CarePlan carePlan = (CarePlan) this.findById(carePlanId);
        CarePlan newCarePlan = carePlan.update(newCarePlanInfo);
        return this.carePlanRepository.save(newCarePlan);
    }

    public GetCarePlanDetailByIDResponseDTO getCarePlanDetailByID(Long carePlanId) {
        CarePlan carePlan = this.findById(carePlanId);
        List<CarePlanVersion> listCarePlanVersion = this.carePlanVersionService.getCarePlanVersionsOfCarePlan(carePlanId);

        return new GetCarePlanDetailByIDResponseDTO(
                carePlan,
                listCarePlanVersion
        );
    }


    public CarePlanVersion submitWaitingForReview(SubmitWaitingForReviewRequest inputData) {

        CarePlanVersion cpv = this.carePlanVersionService.findById(inputData.versionId());

        this.carePlanVersionService.checkValidState(cpv.getId());

        cpv.submitForReview();

        return cpv;
    }

    public CarePlanVersion rejectVersion(RejectCarePlanVersionRequest inputData) {
        return this.carePlanVersionService.reject(
                inputData.versionId(), inputData.actorId(), inputData.rejectReason());
    }


    private CarePlan findById(Long id) {
        return this.carePlanRepository.findById(id).orElseThrow(() -> new NotFoundException("CarePlan not found"));
    }

    private void checkOneCarePlanOpenForAdmission(Long admissionId) {
        List<CarePlan> listCarePlan = this.carePlanRepository.findAllByAdmissionId(admissionId);
        for (CarePlan cp : listCarePlan) {
            if (cp.isOpened())
                throw new IllegalStateException("CarePlan already open for admission id " + admissionId);
        }

    }

//    protected CarePlan buildCarePlanFromRequest(CreateCarePlanRequest inputData) {
//        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
//        return CarePlan.builder()
//                .residentId(inputData.residentId())
//                .admissionId(inputData.admissionId())
//                .createdBy(inputData.creatorId())
//                .updatedBy(inputData.creatorId())
//                .state(CarePlan.CarePlanState.OPEN)
//                .currentVersion(null)
//                .currentVersionId(null)
//                .closedAt(null)
//                .openedAt(now)
//                .createdAt(now)
//                .updatedAt(now)
//                .closedReason(null)
//                .versions(null)
//                .build();
//    }

    private void checkExistResident(Long id) {
        boolean flag = this.residentRepository.existsById(id);
        if (!flag) {
            throw new NotFoundException("No Resident found");
        } else {

        }
    }

    private void checkExistAdmission(long id) {

        boolean flag = this.admissionRepository.existsById(id);
        if (!flag) {
            throw new NotFoundException("No admission found");
        }
    }

    protected void checkValidDates(Object inputData) {
        if (inputData == null) {
            throw new NullPointerException("Inputdata is null");
        }
        Class<?> clazz = inputData.getClass();

        Field[] fields = clazz.getDeclaredFields();

        HashMap<String, String> kvPair = new HashMap<>();

        for (Field f : fields) {
            f.setAccessible(true);
            kvPair.put(f.getName(), f.getType().getName());
        }
        String offsetDateTime = java.time.OffsetDateTime.class.getName();
        String localDate = java.time.LocalDate.class.getName();


    }


}
