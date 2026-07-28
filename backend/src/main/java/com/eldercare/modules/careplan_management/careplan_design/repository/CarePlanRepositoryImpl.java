package com.eldercare.modules.careplan_management.careplan_design.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.dto.searchCarePlanDTO.SearchCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.resident_info.CarePlanResidentInfoEntity;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CareGoalMapper;
import com.eldercare.modules.careplan_management.careplan_design.mapper.CarePlanMapper;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareGoalSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CareInterventionSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.database_schema.CarePlanSchema;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaAssessmentRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCareInterventionRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.jpa.JpaCarePlanRepository;
import com.eldercare.modules.careplan_management.careplan_design.service.ICarePlanRepository;

import jakarta.transaction.Transactional;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;

@Repository

public class CarePlanRepositoryImpl implements ICarePlanRepository {

    private final EntityManager entityManager;
    private final JpaCarePlanRepository jpaCarePlanRepository;
    private final JpaCareGoalRepository jpaCareGoalRepository;
    private final JpaCareInterventionRepository jpaCareInterventionRepository;
    private final ResidentRepository jpaResidentRepositoty;
    private final UserRepository jpaUserRepository;
    private final JpaAssessmentRepository jpaAssessmentRepository;

    public CarePlanRepositoryImpl(EntityManager entityManager, JpaCarePlanRepository jpaCarePlanRepository,
            JpaCareGoalRepository jpaCareGoalRepository,
            JpaCareInterventionRepository jpaCareInterventionRepository, ResidentRepository jpaResidentRepositoty,
            UserRepository jpaUserRepository, JpaAssessmentRepository jpaAssessmentRepository) {
        this.entityManager = entityManager;
        this.jpaCarePlanRepository = jpaCarePlanRepository;
        this.jpaCareGoalRepository = jpaCareGoalRepository;
        this.jpaCareInterventionRepository = jpaCareInterventionRepository;
        this.jpaResidentRepositoty = jpaResidentRepositoty;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaAssessmentRepository = jpaAssessmentRepository;
    }

    @Override
    @Transactional()
    public CarePlanEntity findById(int id) {
        Optional<CarePlanSchema> optionalCarePlanSchema = this.jpaCarePlanRepository.findById(Long.valueOf(id));
        if (optionalCarePlanSchema.isEmpty()) {
            throw new IllegalArgumentException("Care plan with id " + id + " not found");
        }
        return CarePlanMapper.toEntity(optionalCarePlanSchema.get());
    }

    @Override
    @Transactional()
    public CarePlanEntity updateOne(CarePlanEntity carePlanEntity) {
        // CarePlanSchema schema = CarePlanMapper.toSchema(carePlanEntity);
        CarePlanSchema schema = jpaCarePlanRepository.findById(
                (long) carePlanEntity.getId())
                .orElseThrow();
        schema.setStatus(carePlanEntity.getStatus().toString());
        schema.setUpdatedAt(carePlanEntity.getUpdatedAt());
        schema.setSignificantChangeFlag(carePlanEntity.getSignificantFlag());
        if (carePlanEntity.getIsDeleted() == true) {
            schema.setIsDeleted(true);
        }
        jpaCarePlanRepository.save(schema);

        return carePlanEntity;
    }

    @Override
    @Transactional()
    public List<CarePlanEntity> getAll(ListCarePlanRequestDTO request) {

        Pageable pageable = PageRequest.of(
                request.page,
                request.size,
                Sort.by(request.sortDir, request.sortBy));

        Specification<CarePlanSchema> spec = Specification.unrestricted();

        if (request.residentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("residentId"), request.residentId));
        }

        if (request.status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), request.status.name()));
        }

        if (request.significantChangeFlag != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("significantChangeFlag"),
                    request.significantChangeFlag));
        }

        Page<CarePlanSchema> page = jpaCarePlanRepository.findAll(spec, pageable);

        List<CarePlanSchema> carePlans = page.getContent();

        if (carePlans.isEmpty()) {
            return List.of();
        }

        List<Long> carePlanIds = carePlans.stream()
                .map(CarePlanSchema::getId)
                .toList();

        Map<Long, List<CareGoalEntity>> goalMap = jpaCareGoalRepository.findByCarePlanIdIn(carePlanIds)
                .stream()
                .collect(Collectors.groupingBy(
                        goal -> goal.getCarePlan().getId(),
                        Collectors.mapping(
                                goal -> {
                                    return CareGoalMapper.toEntity(goal);
                                },
                                toList())));

        // Map<Long, List<CareInterventionEntity>> interventionMap =
        // jpaCareInterventionRepository
        // .findByCarePlanIdIn(carePlanIds)
        // .stream()
        // .collect(Collectors.groupingBy(
        // intervention -> intervention.getCarePlan().getId(),
        // Collectors.mapping(
        // intervention -> new CareInterventionEntity(
        // intervention.getId().intValue(),
        // intervention.getAssignedRole()),
        // toList())));

        return carePlans.stream()
                .map(schema -> {
                    return CarePlanMapper.toEntity(schema);
                })
                .toList();
    }

    @Override
    @Transactional()
    public Page<CarePlanEntity> getAllPagination(ListCarePlanRequestDTO request) {
        Pageable pageable = PageRequest.of(
                request.page,
                request.size,
                Sort.by(request.sortDir, request.sortBy));

        Specification<CarePlanSchema> spec = Specification.unrestricted();

        if (request.residentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("residentId"), request.residentId));
        }

        if (request.status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), request.status.name()));
        }

        if (request.significantChangeFlag != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("significantChangeFlag"),
                    request.significantChangeFlag));
        }

        Page<CarePlanSchema> page = jpaCarePlanRepository.findAll(spec, pageable);

        List<CarePlanSchema> carePlans = page.getContent();

        if (carePlans.isEmpty()) {
            return Page.empty(pageable);
        }
        List<Long> carePlanIds = carePlans.stream()
                .map(CarePlanSchema::getId)
                .toList();

        Map<Long, List<CareGoalEntity>> goalMap = jpaCareGoalRepository.findByCarePlanIdIn(carePlanIds)
                .stream()
                .collect(Collectors.groupingBy(
                        goal -> goal.getCarePlan().getId(),
                        Collectors.mapping(
                                goal -> CareGoalMapper.toEntity(goal),
                                toList())));

        // Map<Long, List<CareInterventionEntity>> interventionMap =
        // jpaCareInterventionRepository
        // .findByCarePlanIdIn(carePlanIds)
        // .stream()
        // .collect(Collectors.groupingBy(
        // intervention -> intervention.getCarePlan().getId(),
        // Collectors.mapping(
        // intervention -> new CareInterventionEntity(
        // intervention.getId().intValue(),
        // intervention.getAssignedRole()),
        // toList())));

        List<CarePlanEntity> entities = carePlans.stream()
                .map(schema -> {
                    ResidentEntity resident = schema.getResident();
                    BedEntity bed = resident.getBed();

                    String roomNumber = null;
                    String bedNumber = null;

                    if (bed != null) {
                        bedNumber = bed.getBedNumber();

                        if (bed.getRoom() != null) {
                            roomNumber = bed.getRoom().getRoomNumber();
                        }
                    }

                    return CarePlanMapper.toEntity(schema);
                })
                .toList();

        return new PageImpl<>(
                entities,
                pageable,
                page.getTotalElements());
    }

    @Override
    @Transactional()
    public List<CarePlanEntity> search(SearchCarePlanRequestDTO request) {

        Pageable pageable = PageRequest.of(
                request.page,
                request.size,
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        Specification<CarePlanSchema> spec = Specification.unrestricted();

        // TODO:
        // Search by resident name / resident id
        // Waiting for Resident module
        if (request.keyword != null && !request.keyword.isBlank()) {
        }

        if (request.status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), request.status.name()));
        }
        if (request.residentName != null && !request.residentName.isBlank()) {
            System.out.println("ENTER FILTER");
            String[] keywords = request.residentName
                    .trim()
                    .toLowerCase()
                    .split("\\s+");

            spec = spec.and((root, query, cb) -> {
                Join<CarePlanSchema, ResidentEntity> resident = root.join("resident");

                List<Predicate> predicates = new ArrayList<>();

                for (String keyword : keywords) {
                    String pattern = "%" + keyword + "%";

                    predicates.add(
                            cb.or(
                                    cb.like(cb.lower(resident.get("firstName")), pattern),
                                    cb.like(cb.lower(cb.coalesce(resident.get("middleName"), "")), pattern),
                                    cb.like(cb.lower(resident.get("lastName")), pattern)));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            });
        }
        if (request.significantChangeFlag != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("significantChangeFlag"),
                    request.significantChangeFlag));
        }

        Page<CarePlanSchema> page = jpaCarePlanRepository.findAll(spec, pageable);

        return page.getContent()
                .stream()
                .map(CarePlanMapper::toEntity)
                .toList();
    }

    @Override
    @Transactional()
    public Page<CarePlanEntity> searchPagination(SearchCarePlanRequestDTO request) {

        Pageable pageable = PageRequest.of(
                request.page,
                request.size,
                Sort.by(Sort.Direction.DESC, "updatedAt"));

        Specification<CarePlanSchema> spec = Specification.unrestricted();

        if (request.residentName != null && !request.residentName.isBlank()) {
            String[] keywords = request.residentName
                    .trim()
                    .toLowerCase()
                    .split("\\s+");

            spec = spec.and((root, query, cb) -> {
                Join<CarePlanSchema, ResidentEntity> resident = root.join("resident");

                List<Predicate> predicates = new ArrayList<>();

                for (String keyword : keywords) {
                    String pattern = "%" + keyword + "%";

                    predicates.add(
                            cb.or(
                                    cb.like(cb.lower(resident.get("firstName")), pattern),
                                    cb.like(cb.lower(cb.coalesce(resident.get("middleName"), "")), pattern),
                                    cb.like(cb.lower(resident.get("lastName")), pattern)));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            });
        }
        if (request.status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), request.status.name()));
        }

        if (request.significantChangeFlag != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("significantChangeFlag"),
                    request.significantChangeFlag));
        }

        Page<CarePlanSchema> page = jpaCarePlanRepository.findAll(spec, pageable);

        if (page.isEmpty()) {
            return Page.empty(pageable);
        }

        List<CarePlanEntity> entities = page.getContent()
                .stream()
                .map(CarePlanMapper::toEntity)
                .toList();

        return new PageImpl<>(
                entities,
                pageable,
                page.getTotalElements());
    }

    @Override
    @Transactional()
    public CarePlanEntity saveOne(CarePlanEntity carePlanEntity) {
        CarePlanSchema carePlanSchema = CarePlanMapper.toSchema(carePlanEntity);
        CarePlanSchema carePlanSchemaSaved = this.jpaCarePlanRepository.save(carePlanSchema);
        CarePlanEntity carePlanEntityAfterSaved = new CarePlanEntity();
        carePlanEntityAfterSaved.setId(carePlanSchemaSaved.getId().intValue());
        return carePlanEntityAfterSaved;
    }

    @Override
    @Transactional()
    public ResidentEntity getResidentInfo(long id) {
        ResidentEntity residentEntity = this.jpaResidentRepositoty.findById(id).get();
        return residentEntity;
    }

    @Override
    @Transactional()
    public List<UserEntity> getListUserByIDs(List<Long> ids) {
        return this.jpaUserRepository.findAllById(ids);
    }

    @Override
    @Transactional()
    public Map<Long, Integer> getLOCTierFromResidentIds(List<Long> ids) {

        return this.jpaAssessmentRepository.findCurrentResidentTier(ids)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Long) row[1]).intValue()));
    }
}
