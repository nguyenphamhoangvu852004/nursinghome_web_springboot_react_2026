package com.eldercare.modules.careplan_management.careplan_design.mapper;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.eldercare.common.enums.CarePlanGoalStatusEnum;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.careplan_management.careplan_design.dto.CarePlanStatusLabelFormat;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.CarePlanOutput;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

public final class CarePlanMapper {

        private CarePlanMapper() {
        }

        public static CarePlanEntity toEntity(CreateCarePlanRequestDTO dto) {
                if (dto == null) {
                        return null;
                }

                CarePlanEntity entity = new CarePlanEntity();
                List<CareGoalEntity> goals = dto.listCareGoal == null
                                ? new ArrayList<>()
                                : dto.listCareGoal.stream()
                                                .map(goalDTO -> {
                                                        CareGoalEntity goal = new CareGoalEntity();
                                                        goal.setName(goalDTO.name);
                                                        goal.setDesiredOutCome(goalDTO.description);
//                                                        goal.setStatus(CarePlanGoalStatusEnum.IN_PROGRESS);

                                                        List<CareInterventionEntity> interventions = goalDTO.listCareIntervention == null
                                                                        ? new ArrayList<>()
                                                                        : goalDTO.listCareIntervention.stream()
                                                                                        .map(interventionDTO -> {
                                                                                                CareInterventionEntity intervention = new CareInterventionEntity();
                                                                                                intervention.setName(
                                                                                                                interventionDTO.name);
                                                                                                intervention.setAssignedRole(
                                                                                                                interventionDTO.assingedRole);
                                                                                                return intervention;
                                                                                        })
                                                                                        .toList();
                                                        goal.setListCareIntervention(interventions);
                                                        return goal;
                                                })
                                                .toList();
                entity.setListCareGoal(goals);
                return entity;
        }

        public static CarePlanOutput toDTO(CarePlanEntity entity, UserEntity author, int locTier) {
                if (entity == null) {
                        return null;
                }

                CarePlanOutput dto = new CarePlanOutput();
                dto.id = entity.getId().intValue();
                dto.status = CarePlanStatusLabelFormat.getLabel(entity);
                dto.significantFlag = entity.getSignificantFlag();
                dto.lastReviewedBy = entity.getLastReviewBy();
                dto.lastReviewedDateTime = entity.getLastReviewDateTime() == null
                                ? null
                                : entity.getLastReviewDateTime().toString();

                OffsetDateTime nextReview = entity.getNextReviewDateTime();
                dto.nextReviewDateTime = nextReview == null
                                ? "-"
                                : nextReview.isBefore(OffsetDateTime.now())
                                                ? "Overdue"
                                                : nextReview.toLocalDate().toString();
                dto.cycle = 90;

                ResidentEntity resident = entity.getResident();
                dto.resident = new CarePlanOutput.CarePlanResidentOutput(
                                resident.getId().intValue(),
                                getResidentFullName(resident),
                                resident.getDateOfBirth().toString());

                String roomNumber = getRoomNumber(resident);
                String bedNumber = getBedNumber(resident);
                dto.definition = new CarePlanOutput.CarePlanResidentDefinitionOutput(
                                roomNumber == null || bedNumber == null ? "" : roomNumber,
                                roomNumber == null || bedNumber == null ? "" : bedNumber);
                dto.LOCTier = locTier;
                dto.goalCount = entity.getListCareGoal().size();
                dto.createdBy = new CarePlanOutput.CarePlanAuthorOutput(
                                author.getId().intValue(),
                                author.getFirstName(),
                                author.getRole().getRoleName());
                dto.interventionCount = 0;
                dto.createdAt = entity.getCreatedAt() == null ? null : entity.getCreatedAt().toString();
                dto.updatedAt = entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().toString();
                dto.isDeleted = entity.getIsDeleted();
                return dto;
        }

        public static CarePlanEntity prepareForRead(CarePlanEntity entity) {
                if (entity == null) {
                        return null;
                }

                if (entity.getCreatedByUser() != null) {
                        entity.setCreatedBy(entity.getCreatedByUser().getId().intValue());
                }

                entity.setLastReviewBy(entity.getLastReviewedByUser() == null
                                ? null
                                : entity.getLastReviewedByUser().getId().toString());
                return entity;
        }

        public static CarePlanEntity prepareForSave(CarePlanEntity entity) {
                if (entity == null) {
                        return null;
                }

                if (entity.getCreatedByUser() == null) {
                        UserEntity user = new UserEntity();
                        user.setId((long) entity.getCreatedBy());
                        entity.setCreatedByUser(user);
                }

                entity.getListCareGoal().forEach(goal -> CareGoalMapper.attachToCarePlan(goal, entity));
                return entity;
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
