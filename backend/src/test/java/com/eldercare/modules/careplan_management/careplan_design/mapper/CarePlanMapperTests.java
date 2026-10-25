package com.eldercare.modules.careplan_management.careplan_design.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.modules.admin.user_management.RoleEntity;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.CarePlanOutput;
import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

class CarePlanMapperTests {

    @Test
    void toEntity_mapsGoalsAndInterventions() {
        CreateCarePlanRequestDTO request = new CreateCarePlanRequestDTO();
        request.listCareGoal = List.of(
                new CreateCarePlanRequestDTO.CreateCarePlanCareGoalRequestDTO(
                        "Maintain mobility",
                        "Resident walks safely",
                        List.of(new CreateCarePlanRequestDTO.CreateCarePlanCareInterventionRequestDTO(
                                "Assist walking",
                                "CAREGIVER"))));

        CarePlanEntity entity = CarePlanMapper.toEntity(request);

        assertNotNull(entity);
        assertEquals(1, entity.getListCareGoal().size());
        assertEquals("Maintain mobility", entity.getListCareGoal().get(0).getName());
        assertEquals(1, entity.getListCareGoal().get(0).getListCareIntervention().size());
        assertEquals("Assist walking",
                entity.getListCareGoal().get(0).getListCareIntervention().get(0).getName());
    }

    @Test
    void toDTO_mapsCarePlanListOutput() {
        ResidentEntity resident = new ResidentEntity();
        resident.setId(10L);
        resident.setFirstName("An");
        resident.setLastName("Nguyen");
        resident.setDateOfBirth(LocalDate.of(1940, 1, 2));

        RoleEntity role = new RoleEntity();
        role.setRoleName("NURSE");

        UserEntity author = new UserEntity();
        author.setId(20L);
        author.setFirstName("Linh");
        author.setRole(role);

        CarePlanEntity entity = new CarePlanEntity();
        entity.setId(1);
        entity.setStatus(CarePlanStatusEnum.DRAFT);
        entity.setSignificantFlag(false);
        entity.setResident(resident);
        entity.setCreatedAt(OffsetDateTime.parse("2026-01-01T08:00:00+07:00"));
        entity.setUpdatedAt(OffsetDateTime.parse("2026-01-02T08:00:00+07:00"));
        entity.setIsDeleted(false);

        CarePlanOutput dto = CarePlanMapper.toDTO(entity, author, 3);

        assertEquals(1, dto.id);
        assertEquals("An Nguyen", dto.resident.fullname);
        assertEquals(3, dto.LOCTier);
        assertEquals("Linh", dto.createdBy.fullname);
        assertEquals("NURSE", dto.createdBy.role);
        assertEquals("", dto.definition.room);
        assertEquals("", dto.definition.bed);
    }
}
