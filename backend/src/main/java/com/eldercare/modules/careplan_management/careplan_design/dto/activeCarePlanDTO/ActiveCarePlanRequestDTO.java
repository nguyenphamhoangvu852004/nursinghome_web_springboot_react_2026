package com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO;

public class ActiveCarePlanRequestDTO {
    public int carePlanId;
    public int donId;
    public int donPassword;
    public ActiveCarePlanRequestDTO(int carePlanId) {
        this.carePlanId = carePlanId;
    }

    public int getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(int carePlanId) {
        this.carePlanId = carePlanId;
    }

}
