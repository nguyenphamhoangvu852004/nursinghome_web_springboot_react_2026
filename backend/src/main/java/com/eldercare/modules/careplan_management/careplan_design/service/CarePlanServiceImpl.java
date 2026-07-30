package com.eldercare.modules.careplan_management.careplan_design.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.enums.CarePlanGoalStatusEnum;
import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.admin.facility_setup.care_level.dto.response.CareLevelRateResponse;
import com.eldercare.modules.admin.facility_setup.care_level.service.CareLevelService;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.careplan_management.careplan_design.dto.CarePlanStatusLabelFormat;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.CarePlanOutput;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.searchCarePlanDTO.SearchCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.resident_info.CarePlanResidentInfoEntity;
import com.eldercare.modules.careplan_management.careplan_design.service.loc_service.ILocRateEstimation;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import jakarta.transaction.Transactional;

@Service
public class CarePlanServiceImpl implements ICarePlanService, ILocRateEstimation {

        private static final Logger log = LoggerFactory.getLogger(CarePlanServiceImpl.class);
        private final ICarePlanRepository carePlanRepository;
        private final CareLevelService careLevelService;

        public CarePlanServiceImpl(ICarePlanRepository carePlanRepository, CareLevelService careLevelService) {
                this.carePlanRepository = carePlanRepository;
                this.careLevelService = careLevelService;
        }

        @Override
        public ActiveCarePlanResponseDTO activateCarePlan(ActiveCarePlanRequestDTO requestDTO) {
                int id = requestDTO.carePlanId;

                CarePlanEntity carePlanEntity = this.carePlanRepository.findById(id);

                // TODO: need to get from context
                // now hardcode DON id
                carePlanEntity.approve("DON-001");
                this.carePlanRepository.updateOne(carePlanEntity);
                return new ActiveCarePlanResponseDTO(carePlanEntity.getId(),
                                CarePlanStatusLabelFormat.getLabel(carePlanEntity),
                                carePlanEntity.getUpdatedAt().toString());
        }

        @Override
        public DiscontinueCarePlanResponseDTO discontinueCarePlan(DiscontinueCarePlanRequestDTO requestDTO) {
                int id = requestDTO.carePlanId;
                CarePlanEntity carePlanEntity = this.carePlanRepository.findById(id);
                carePlanEntity.archive();
                this.carePlanRepository.updateOne(carePlanEntity);
                return new DiscontinueCarePlanResponseDTO(
                                carePlanEntity.getId(), CarePlanStatusLabelFormat.getLabel(carePlanEntity),
                                carePlanEntity.getUpdatedAt().toString());

        }

        @Override
        public MarkSignificantChangeResponseDTO markSignificantChange(MarkSignificantChangeRequestDTO requestDTO) {
                int id = requestDTO.carePlanId;
                CarePlanEntity carePlanEntity = this.carePlanRepository.findById(id);
                carePlanEntity.markSignificant();
                this.carePlanRepository.updateOne(carePlanEntity);
                return new MarkSignificantChangeResponseDTO(carePlanEntity.getId(), carePlanEntity.getSignificantFlag(),
                                carePlanEntity.getUpdatedAt().toString());

        }

        @Override
        @Transactional()
        public PagedResponse<ListCarePlanResponseDTO> listCarePlans(ListCarePlanRequestDTO requestDTO) {
                // List<CarePlanEntity> listCarePlanEntity =
                // carePlanRepository.getAll(requestDTO);
                Page<CarePlanEntity> pageCarePlanEntity = carePlanRepository.getAllPagination(requestDTO);
                List<CarePlanEntity> listCarePlanEntity = pageCarePlanEntity.getContent();
                List<Long> userIds = listCarePlanEntity.stream()
                                .map(entity -> Long.valueOf(entity.getCreatedBy()))
                                .distinct()
                                .toList();

                List<UserEntity> authors = this.carePlanRepository.getListUserByIDs(userIds);

                Map<Long, UserEntity> authorMap = authors.stream()
                                .collect(Collectors.toMap(
                                                UserEntity::getId,
                                                Function.identity()));
                List<Long> residentIds = listCarePlanEntity.stream()
                                .map(entity -> Long.valueOf(entity.getResident().getId()))
                                .distinct()
                                .toList();
                Map<Long, Integer> locTierMap = this.carePlanRepository.getLOCTierFromResidentIds(residentIds);
                List<CarePlanOutput> listCarePlanOutputs = listCarePlanEntity.stream()
                                .map(entity -> {
                                        CarePlanOutput output = new CarePlanOutput();
                                        output.id = entity.getId();
                                        output.status = CarePlanStatusLabelFormat.getLabel(entity);
                                        output.significantFlag = entity.getSignificantFlag();
                                        output.lastReviewedBy = entity.getLastReviewBy() == null ? null
                                                        : entity.getLastReviewBy();
                                        output.lastReviewedDateTime = entity.getLastReviewDateTime() == null ? null
                                                        : entity.getLastReviewDateTime().toString();

                                        OffsetDateTime nextReview = entity.getNextReviewDateTime();

                                        output.nextReviewDateTime = nextReview == null
                                                        ? "-"
                                                        : nextReview.isBefore(OffsetDateTime.now())
                                                                        ? "Overdue"
                                                                        : nextReview.toLocalDate()
                                                                                        .toString();

                                        output.cycle = 90;
                                        output.resident = new CarePlanOutput.CarePlanResidentOutput(
                                                        entity.getResident().getId(),
                                                        entity.getResident().getFullname(),
                                                        entity.getResident().getDob().toString());
                                        if (entity.getResident().getRoom() == null
                                                        || entity.getResident().getBed() == null) {

                                                output.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                                                "", "");

                                        } else {

                                                output.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                                                entity.getResident().getRoom(),
                                                                entity.getResident().getBed());
                                        }
                                        output.LOCTier = locTierMap.getOrDefault(
                                                        Long.valueOf(entity.getResident().getId()),
                                                        0);
                                        ;
                                        output.goalCount = entity.getListCareGoal().size();
                                        UserEntity author = authorMap.get(Long.valueOf(entity.getCreatedBy()));
                                        output.createdBy = new CarePlanOutput.CarePlanAuthorOutput(
                                                        author.getId().intValue(),
                                                        author.getFirstName(),
                                                        author.getRole().getRoleName());
                                        output.interventionCount = 0;
                                        output.createdAt = entity.getCreatedAt() == null
                                                        ? null
                                                        : entity.getCreatedAt().toString();
                                        output.updatedAt = entity.getUpdatedAt() == null
                                                        ? null
                                                        : entity.getUpdatedAt().toString();
                                        output.isDeleted = entity.getIsDeleted();

                                        return output;
                                })
                                .toList();
                return PagedResponse.of(
                                new ListCarePlanResponseDTO(listCarePlanOutputs),
                                HttpStatus.OK.value(),
                                "Success",
                                pageCarePlanEntity.getNumber(),
                                pageCarePlanEntity.getTotalPages(),
                                pageCarePlanEntity.getSize(),
                                pageCarePlanEntity.getTotalElements());
        }

        @Override
        @Transactional()
        public GetCarePlanDetailResponseDTO getCarePlanDetail(GetCarePlanDetailRequestDTO requestDTO) {
                double randomBedRate = ThreadLocalRandom.current().nextDouble(30.0, 200.0);

                CarePlanEntity carePlanEntity = this.carePlanRepository.findById(requestDTO.id);
                List<Long> authorIds = new ArrayList<>();
                authorIds.add((long) carePlanEntity.getCreatedBy());
                List<UserEntity> author = this.carePlanRepository
                                .getListUserByIDs(authorIds);
                List<Long> residentIds = new ArrayList<>();
                residentIds.add((long) carePlanEntity.getResident().getId());
                Map<Long, Integer> locTierMap = this.carePlanRepository.getLOCTierFromResidentIds(residentIds);
                System.out.println(locTierMap);
                List<CareLevelRateResponse> listCareLevelRateResponses = this.careLevelService
                                .getCareLevelRates((long) locTierMap.get((long) carePlanEntity.getResident().getId()));

                CareLevelRateResponse careLevelRateResponse = listCareLevelRateResponses.get(0);
                String dailyCostEstimate = this.calculateCostDaily(careLevelRateResponse.getDailyRate().doubleValue(),
                                randomBedRate);
                String monthyCostEstimate = this
                                .calculateCostMonthly(careLevelRateResponse.getDailyRate().doubleValue(),
                                                randomBedRate);

                GetCarePlanDetailResponseDTO responseDTO = new GetCarePlanDetailResponseDTO();
                responseDTO.costEstimation = new GetCarePlanDetailResponseDTO.CostEstimation(dailyCostEstimate,
                                monthyCostEstimate, String.valueOf(careLevelRateResponse.getDailyRate().doubleValue()),
                                String.valueOf(String.format("%.2f", randomBedRate)));
                responseDTO.id = carePlanEntity.getId();
                responseDTO.status = CarePlanStatusLabelFormat.getLabel(carePlanEntity);
                responseDTO.significantFlag = carePlanEntity.getSignificantFlag();
                responseDTO.lastReviewedBy = carePlanEntity.getLastReviewBy() == null ? null
                                : carePlanEntity.getLastReviewBy();
                responseDTO.lastReviewedDateTime = carePlanEntity.getLastReviewDateTime() == null ? null
                                : carePlanEntity.getLastReviewDateTime().toString();
                responseDTO.nextReviewDateTime = carePlanEntity.getNextReviewDateTime() == null ? null
                                : carePlanEntity.getNextReviewDateTime().toString();
                responseDTO.cycle = 90;

                responseDTO.resident = new CarePlanOutput.CarePlanResidentOutput(
                                carePlanEntity.getResident().getId(),
                                carePlanEntity.getResident().getFullname().toString(),
                                carePlanEntity.getResident().getDob().toString());
                responseDTO.createdAt = carePlanEntity.getCreatedAt().toString();
                responseDTO.updatedAt = carePlanEntity.getUpdatedAt().toString();
                responseDTO.isDeleted = carePlanEntity.getIsDeleted();
                responseDTO.createdBy = new CarePlanOutput.CarePlanAuthorOutput(author.get(0).getId().intValue(),
                                author.get(0).getFirstName(), author.get(0).getRole().getRoleName());
                responseDTO.locTier = locTierMap.getOrDefault(
                                Long.valueOf(carePlanEntity.getResident().getId()),
                                0);
                ;
                responseDTO.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                carePlanEntity.getResident().getRoom(),
                                carePlanEntity.getResident().getBed());
                responseDTO.goals = carePlanEntity.getListCareGoal()
                                .stream()
                                .map(goal -> {
                                        GetCarePlanDetailResponseDTO.Goal dto = new GetCarePlanDetailResponseDTO.Goal();
                                        dto.id = goal.getId();
                                        dto.goalDescription = goal.getDescription();
                                        dto.title = goal.getName();
                                        dto.status = goal.getStatus().name();
                                        dto.interventions = goal.getListCareIntervention()
                                                        .stream()
                                                        .map(intervention -> {
                                                                GetCarePlanDetailResponseDTO.Intervention interventionDto = new GetCarePlanDetailResponseDTO.Intervention();

                                                                interventionDto.id = intervention.getId();
                                                                interventionDto.title = intervention.getName();
                                                                interventionDto.assignedRole = intervention
                                                                                .getAssinedRole();

                                                                return interventionDto;
                                                        })
                                                        .toList();

                                        return dto;
                                })
                                .toList();

                return responseDTO;
        }

        @Override
        @Transactional
        public PagedResponse<ListCarePlanResponseDTO> searchCarePlan(
                        SearchCarePlanRequestDTO requestDTO) {

                Page<CarePlanEntity> pageCarePlanEntity = carePlanRepository.searchPagination(requestDTO);

                List<CarePlanEntity> listCarePlanEntity = pageCarePlanEntity.getContent();

                List<Long> userIds = listCarePlanEntity.stream()
                                .map(entity -> Long.valueOf(entity.getCreatedBy()))
                                .distinct()
                                .toList();

                List<UserEntity> authors = carePlanRepository.getListUserByIDs(userIds);

                Map<Long, UserEntity> authorMap = authors.stream()
                                .collect(Collectors.toMap(
                                                UserEntity::getId,
                                                Function.identity()));

                List<Long> residentIds = listCarePlanEntity.stream()
                                .map(entity -> Long.valueOf(entity.getResident().getId()))
                                .distinct()
                                .toList();

                Map<Long, Integer> locTierMap = carePlanRepository.getLOCTierFromResidentIds(residentIds);

                List<CarePlanOutput> outputs = listCarePlanEntity.stream()
                                .map(entity -> {

                                        CarePlanOutput output = new CarePlanOutput();

                                        output.id = entity.getId();
                                        output.status = CarePlanStatusLabelFormat.getLabel(entity);
                                        output.significantFlag = entity.getSignificantFlag();

                                        output.lastReviewedBy = entity.getLastReviewBy();

                                        output.lastReviewedDateTime = entity.getLastReviewDateTime() == null
                                                        ? null
                                                        : entity.getLastReviewDateTime().toString();

                                        OffsetDateTime nextReview = entity.getNextReviewDateTime();

                                        output.nextReviewDateTime = nextReview == null
                                                        ? "-"
                                                        : nextReview.isBefore(OffsetDateTime.now())
                                                                        ? "Overdue"
                                                                        : nextReview.toLocalDate().toString();

                                        output.cycle = 90;

                                        output.resident = new CarePlanOutput.CarePlanResidentOutput(
                                                        entity.getResident().getId(),
                                                        entity.getResident().getFullname(),
                                                        entity.getResident().getDob().toString());

                                        if (entity.getResident().getRoom() == null
                                                        || entity.getResident().getBed() == null) {

                                                output.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                                                "", "");

                                        } else {

                                                output.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                                                entity.getResident().getRoom(),
                                                                entity.getResident().getBed());
                                        }

                                        output.LOCTier = locTierMap.getOrDefault(
                                                        Long.valueOf(entity.getResident().getId()),
                                                        0);

                                        output.goalCount = entity.getListCareGoal().size();

                                        UserEntity author = authorMap.get(Long.valueOf(entity.getCreatedBy()));

                                        output.createdBy = new CarePlanOutput.CarePlanAuthorOutput(
                                                        author.getId().intValue(),
                                                        author.getFirstName(),
                                                        author.getRole().getRoleName());

                                        output.interventionCount = 0;

                                        output.createdAt = entity.getCreatedAt() == null
                                                        ? null
                                                        : entity.getCreatedAt().toString();

                                        output.updatedAt = entity.getUpdatedAt() == null
                                                        ? null
                                                        : entity.getUpdatedAt().toString();

                                        output.isDeleted = entity.getIsDeleted();

                                        return output;
                                })
                                .toList();

                return PagedResponse.of(
                                new ListCarePlanResponseDTO(outputs),
                                HttpStatus.OK.value(),
                                "Success",
                                pageCarePlanEntity.getNumber(),
                                pageCarePlanEntity.getTotalPages(),
                                pageCarePlanEntity.getSize(),
                                pageCarePlanEntity.getTotalElements());
        }

        @Override
        public CreateCarePlanResponseDTO createCarePlan(CreateCarePlanRequestDTO requestDTO, String purpose) {
                // create care plan entity
                CarePlanEntity carePlan = new CarePlanEntity();
                carePlan.setLastReviewBy(null);
                carePlan.setCreatedAt(OffsetDateTime.now());
                carePlan.setUpdatedAt(OffsetDateTime.now());
                carePlan.setIsDeleted(false);
                carePlan.setLastReviewDateTime(null);

                if (purpose != null) {
                        switch (purpose) {
                                case "NEED_REVIEW":
                                        carePlan.setStatus(CarePlanStatusEnum.PENDING_REVIEW);
                                        break;
                                default:
                                        carePlan.setStatus(CarePlanStatusEnum.DRAFT);
                                        break;
                        }
                } else {
                        carePlan.setStatus(CarePlanStatusEnum.DRAFT);
                }

                // check list care goal
                if (requestDTO.listCareGoal == null || requestDTO.listCareGoal.size() < 1) {
                        throw new RuntimeException("Care plan need at least one care goal");
                }

                // TODO: find resident info
                int residentId = requestDTO.residentId;
                ResidentEntity resident = this.carePlanRepository.getResidentInfo((long) residentId);

                // TODO: check chart lock ,...
                if (resident.isChartLocked() == true) {
                        throw new RuntimeException("Resident is locked, can not create care plan for this resident");
                }

                CarePlanResidentInfoEntity carePlanResidentInfoEntity = new CarePlanResidentInfoEntity();
                carePlanResidentInfoEntity.setId(resident.getId().intValue());
                carePlan.setResident(carePlanResidentInfoEntity);

                List<CareGoalEntity> goals = requestDTO.listCareGoal.stream()
                                .map(goalDto -> {

                                        CareGoalEntity goal = new CareGoalEntity();
                                        goal.setName(goalDto.name);
                                        goal.setDescription(goalDto.description);
                                        goal.setStatus(CarePlanGoalStatusEnum.IN_PROGRESS);

                                        List<CareInterventionEntity> interventions = goalDto.listCareIntervention
                                                        .stream()
                                                        .map(interventionDto -> {
                                                                CareInterventionEntity intervention = new CareInterventionEntity();

                                                                intervention.setName(interventionDto.name);
                                                                intervention.setAssinedRole(
                                                                                interventionDto.assingedRole);
                                                                return intervention;
                                                        })
                                                        .toList();

                                        goal.setListCareIntervention(interventions);

                                        return goal;
                                })
                                .toList();

                carePlan.setListCareGoal(goals);

                System.out.println(carePlan);

                // TODO: call repo to save
                CarePlanEntity carePlanEntityAfterSaved = this.carePlanRepository.saveOne(carePlan);

                CreateCarePlanResponseDTO responseDTO = new CreateCarePlanResponseDTO();
                responseDTO.id = carePlanEntityAfterSaved.getId();
                return responseDTO;
        }

        @Override
        public DeleteCarePlanResponseDTO softDeleteCarePlan(DeleteCarePlanRequestDTO requestDTO) {
                CarePlanEntity carePlanEntity = this.carePlanRepository.findById(requestDTO.carePlanId);
                carePlanEntity.softDelete();
                CarePlanEntity carePlanEntityAfterUpdated = this.carePlanRepository.updateOne(carePlanEntity);

                DeleteCarePlanResponseDTO responseDTo = new DeleteCarePlanResponseDTO(
                                carePlanEntityAfterUpdated.getId(), carePlanEntityAfterUpdated.getIsDeleted());
                return responseDTo;
        }

        @Override
        public String calculateCostDaily(double locTierRate, double bedRate) {
                double result;
                double dailyLocRate = locTierRate / 30;
                result = dailyLocRate + bedRate;

                return String.valueOf(String.format("%.2f", result));
        }

        @Override
        public String calculateCostMonthly(double locTierRate, double bedRate) {
                double result;
                result = locTierRate + (bedRate * 30);
                return String.valueOf(String.format("%.2f", result));
        }

}
