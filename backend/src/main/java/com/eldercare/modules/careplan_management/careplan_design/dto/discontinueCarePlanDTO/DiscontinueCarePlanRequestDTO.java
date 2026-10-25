package com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO;

public class DiscontinueCarePlanRequestDTO {
     public int carePlanId;

    public DiscontinueCarePlanRequestDTO(int carePlanId) {
        this.carePlanId = carePlanId;
    }

    public int getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(int carePlanId) {
        this.carePlanId = carePlanId;
    }
}
