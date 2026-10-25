package com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO;

public class AddCareGoalResponseDTO {
    public int carePlanId;
    public int careGoalId;

    public AddCareGoalResponseDTO(int carePlanId, int careGoalId) {
        this.carePlanId = carePlanId;
        this.careGoalId = careGoalId;
    }

}
