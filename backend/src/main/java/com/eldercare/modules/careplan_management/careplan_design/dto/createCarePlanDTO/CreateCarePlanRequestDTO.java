package com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class CreateCarePlanRequestDTO {
    public int residentId;
    public List<CreateCarePlanCareGoalRequestDTO> listCareGoal;


    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCarePlanCareGoalRequestDTO{
        public String name;
        public String description;
        public List<CreateCarePlanCareInterventionRequestDTO> listCareIntervention;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCarePlanCareInterventionRequestDTO{
        public String name;
        public String assingedRole;
    }
}
