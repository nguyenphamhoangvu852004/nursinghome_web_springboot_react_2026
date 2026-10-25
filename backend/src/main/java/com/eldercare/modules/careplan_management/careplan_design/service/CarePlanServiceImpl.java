package com.eldercare.modules.careplan_management.careplan_design.service;

import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CarePlanMapper;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaAssessmentRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaCarePlanRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailResponseDTO;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.eldercare.modules.careplan_management.careplan_design.dto.CarePlanStatusLabelFormat;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.CarePlanOutput;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;

@Service
public class CarePlanServiceImpl implements ICarePlanService {

        private final JpaCarePlanRepository carePlanRepository;
        private final ResidentRepository residentRepository;
        private final UserRepository userRepository;
        private final JpaAssessmentRepository assessmentRepository;

        public CarePlanServiceImpl(JpaCarePlanRepository carePlanRepository,
                        ResidentRepository residentRepository,
                        UserRepository userRepository,
                        JpaAssessmentRepository assessmentRepository) {
                this.carePlanRepository = carePlanRepository;
                this.residentRepository = residentRepository;
                this.userRepository = userRepository;
                this.assessmentRepository = assessmentRepository;
        }

        @Override
        public ActiveCarePlanResponseDTO activateCarePlan(ActiveCarePlanRequestDTO requestDTO) {
                int id = requestDTO.carePlanId;

                CarePlanEntity carePlanEntity = findCarePlanById(id);

                // TODO: need to get from context
                // now hardcode DON id
                carePlanEntity.approve("DON-001");
                updateCarePlan(carePlanEntity);
                return new ActiveCarePlanResponseDTO(carePlanEntity.getId().intValue(),
                                CarePlanStatusLabelFormat.getLabel(carePlanEntity),
                                carePlanEntity.getUpdatedAt().toString());
        }

        @Override
        public DiscontinueCarePlanResponseDTO discontinueCarePlan(DiscontinueCarePlanRequestDTO requestDTO) {
                int id = requestDTO.carePlanId;
                CarePlanEntity carePlanEntity = findCarePlanById(id);
                carePlanEntity.archive();
                updateCarePlan(carePlanEntity);
                return new DiscontinueCarePlanResponseDTO(
                                carePlanEntity.getId().intValue(), CarePlanStatusLabelFormat.getLabel(carePlanEntity),
                                carePlanEntity.getUpdatedAt().toString());

        }

        @Override
        public MarkSignificantChangeResponseDTO markSignificantChange(MarkSignificantChangeRequestDTO requestDTO) {
                int id = requestDTO.carePlanId;
                CarePlanEntity carePlanEntity = findCarePlanById(id);
                carePlanEntity.markSignificant();
                updateCarePlan(carePlanEntity);
                return new MarkSignificantChangeResponseDTO(carePlanEntity.getId().intValue(), carePlanEntity.getSignificantFlag(),
                                carePlanEntity.getUpdatedAt().toString());

        }

        @Override
        @Transactional()
        public PagedResponse<ListCarePlanResponseDTO> listCarePlans(ListCarePlanRequestDTO requestDTO) {
                Page<CarePlanEntity> pageCarePlanEntity = findAllCarePlans(requestDTO);
                List<CarePlanEntity> listCarePlanEntity = pageCarePlanEntity.getContent();
                // get distinct user chạy query rồi map, sau khỏi phải bị n+1
                List<Long> userIds = listCarePlanEntity.stream()
                                .map(entity -> Long.valueOf(entity.getCreatedBy()))
                                .distinct()
                                .toList();

                List<UserEntity> authors = this.userRepository.findAllById(userIds);

                Map<Long, UserEntity> authorMap = authors.stream()
                                .collect(Collectors.toMap(
                                                UserEntity::getId,
                                                Function.identity()));
                List<Long> residentIds = listCarePlanEntity.stream()
                                .map(entity -> entity.getResident().getId())
                                .distinct()
                                .toList();
                Map<Long, Integer> locTierMap = getLOCTierFromResidentIds(residentIds);
                List<CarePlanOutput> listCarePlanOutputs = listCarePlanEntity.stream()
                                .map(entity -> CarePlanMapper.toDTO(
                                                entity,
                                                authorMap.get(Long.valueOf(entity.getCreatedBy())),
                                                locTierMap.getOrDefault(entity.getResident().getId(), 0)))
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

                CarePlanEntity carePlanEntity = findCarePlanById(requestDTO.id);
                List<Long> authorIds = new ArrayList<>();
                authorIds.add((long) carePlanEntity.getCreatedBy());
                List<UserEntity> author = this.userRepository.findAllById(authorIds);
                List<Long> residentIds = new ArrayList<>();
                residentIds.add(carePlanEntity.getResident().getId());
                Map<Long, Integer> locTierMap = getLOCTierFromResidentIds(residentIds);

                GetCarePlanDetailResponseDTO responseDTO = new GetCarePlanDetailResponseDTO();
                responseDTO.id = carePlanEntity.getId().intValue();
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
                                carePlanEntity.getResident().getId().intValue(),
                                getResidentFullName(carePlanEntity.getResident()),
                                carePlanEntity.getResident().getDateOfBirth().toString());
                responseDTO.createdAt = carePlanEntity.getCreatedAt().toString();
                responseDTO.updatedAt = carePlanEntity.getUpdatedAt().toString();
                responseDTO.isDeleted = carePlanEntity.getIsDeleted();
                responseDTO.createdBy = new CarePlanOutput.CarePlanAuthorOutput(author.get(0).getId().intValue(),
                                author.get(0).getFirstName(), author.get(0).getRole().getRoleName());
                responseDTO.locTier = locTierMap.getOrDefault(
                                carePlanEntity.getResident().getId(),
                                0);
                ;
                responseDTO.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                getRoomNumber(carePlanEntity.getResident()),
                                getBedNumber(carePlanEntity.getResident()));
                responseDTO.goals = carePlanEntity.getListCareGoal()
                                .stream()
                                .map(goal -> {
                                        GetCarePlanDetailResponseDTO.Goal dto = new GetCarePlanDetailResponseDTO.Goal();
                                        dto.id = goal.getId().intValue();
                                        dto.goalDescription = goal.getDesiredOutCome();
                                        dto.title = goal.getName();
//                                        dto.status = goal.getStatus().name();
                                        dto.interventions = goal.getListCareIntervention()
                                                        .stream()
                                                        .map(intervention -> {
                                                                GetCarePlanDetailResponseDTO.Intervention interventionDto = new GetCarePlanDetailResponseDTO.Intervention();

                                                                interventionDto.id = intervention.getId().intValue();
                                                                interventionDto.title = intervention.getName();
                                                                interventionDto.assignedRole = intervention
                                                                                .getAssignedRole();

                                                                return interventionDto;
                                                        })
                                                        .toList();

                                        return dto;
                                })
                                .toList();

                return responseDTO;
        }

        @Override
        public CreateCarePlanResponseDTO createCarePlan(CreateCarePlanRequestDTO requestDTO, String purpose) {
                // create care plan entity
                CarePlanEntity carePlan = CarePlanMapper.toEntity(requestDTO);
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
                ResidentEntity resident = this.residentRepository.findById((long) residentId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Resident with id " + residentId + " not found"));

                // TODO: check chart lock ,...
                if (resident.isChartLocked() == true) {
                        throw new RuntimeException("Resident is locked, can not create care plan for this resident");
                }

                carePlan.setResident(resident);

                System.out.println(carePlan);

                // TODO: call repo to save
                CarePlanMapper.prepareForSave(carePlan);
                CarePlanEntity carePlanEntityAfterSaved = this.carePlanRepository.save(carePlan);

                CreateCarePlanResponseDTO responseDTO = new CreateCarePlanResponseDTO();
                responseDTO.id = carePlanEntityAfterSaved.getId().intValue();
                return responseDTO;
        }

        @Override
        public DeleteCarePlanResponseDTO softDeleteCarePlan(DeleteCarePlanRequestDTO requestDTO) {
                CarePlanEntity carePlanEntity = findCarePlanById(requestDTO.carePlanId);
                carePlanEntity.softDelete();
                CarePlanEntity carePlanEntityAfterUpdated = updateCarePlan(carePlanEntity);

                DeleteCarePlanResponseDTO responseDTo = new DeleteCarePlanResponseDTO(
                                carePlanEntityAfterUpdated.getId().intValue(), carePlanEntityAfterUpdated.getIsDeleted());
                return responseDTo;
        }

        private CarePlanEntity findCarePlanById(int id) {
                return carePlanRepository.findById((long) id)
                                .map(CarePlanMapper::prepareForRead)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Care plan with id " + id + " not found"));
        }

        private CarePlanEntity updateCarePlan(CarePlanEntity carePlanEntity) {
                CarePlanEntity persistedCarePlan = carePlanRepository.findById(carePlanEntity.getId())
                                .orElseThrow();
                persistedCarePlan.setStatus(carePlanEntity.getStatus());
                persistedCarePlan.setUpdatedAt(carePlanEntity.getUpdatedAt());
                persistedCarePlan.setSignificantFlag(carePlanEntity.getSignificantFlag());
                if (Boolean.TRUE.equals(carePlanEntity.getIsDeleted())) {
                        persistedCarePlan.setIsDeleted(true);
                }
                carePlanRepository.save(persistedCarePlan);
                return carePlanEntity;
        }

        private Page<CarePlanEntity> findAllCarePlans(ListCarePlanRequestDTO request) {
                Pageable pageable = PageRequest.of(
                                request.page,
                                request.size,
                                Sort.by(request.sortDir, request.sortBy));

                Specification<CarePlanEntity> specification = Specification.unrestricted();
                if (request.residentId != null) {
                        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                                        root.get("resident").get("id"), request.residentId));
                }
                if (request.residentName != null && !request.residentName.isBlank()) {
                        String[] nameParts = request.residentName.trim().toLowerCase().split("\\s+");
                        specification = specification.and((root, query, criteriaBuilder) -> {
                                Join<CarePlanEntity, ResidentEntity> resident = root.join("resident");
                                List<Predicate> predicates = new ArrayList<>();
                                for (String namePart : nameParts) {
                                        String pattern = "%" + namePart + "%";
                                        predicates.add(criteriaBuilder.or(
                                                        criteriaBuilder.like(criteriaBuilder.lower(
                                                                        resident.get("firstName")), pattern),
                                                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(
                                                                        resident.get("middleName"), "")), pattern),
                                                        criteriaBuilder.like(criteriaBuilder.lower(
                                                                        resident.get("lastName")), pattern)));
                                }
                                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                        });
                }
                if (request.status != null) {
                        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                                        root.get("status"), request.status));
                }
                if (request.significantChangeFlag != null) {
                        specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(
                                        root.get("significantFlag"), request.significantChangeFlag));
                }

                return preparePageForRead(carePlanRepository.findAll(specification, pageable), pageable);
        }

        private Page<CarePlanEntity> preparePageForRead(Page<CarePlanEntity> page, Pageable pageable) {
                if (page.isEmpty()) {
                        return Page.empty(pageable);
                }
                List<CarePlanEntity> entities = page.getContent().stream()
                                .map(CarePlanMapper::prepareForRead)
                                .toList();
                return new PageImpl<>(entities, pageable, page.getTotalElements());
        }

        private Map<Long, Integer> getLOCTierFromResidentIds(List<Long> residentIds) {
                return assessmentRepository.findCurrentResidentTier(residentIds).stream()
                                .collect(Collectors.toMap(
                                                row -> (Long) row[0],
                                                row -> ((Long) row[1]).intValue()));
        }

        private static String getResidentFullName(ResidentEntity resident) {
                return Stream.of(resident.getFirstName(), resident.getMiddleName(), resident.getLastName())
                                .filter(namePart -> namePart != null && !namePart.isBlank())
                                .collect(Collectors.joining(" "));
        }

        private static String getRoomNumber(ResidentEntity resident) {
                if (resident.getBed() == null || resident.getBed().getRoom() == null) {
                        return null;
                }
                return resident.getBed().getRoom().getRoomNumber();
        }

        private static String getBedNumber(ResidentEntity resident) {
                return resident.getBed() == null ? null : resident.getBed().getBedNumber();
        }

}
