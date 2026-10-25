package com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO;

public class MarkSignificantChangeRequestDTO {
    public int carePlanId;

    public MarkSignificantChangeRequestDTO(int carePlanId) {
        this.carePlanId = carePlanId;
    }

    public int getCarePlanId() {
        return carePlanId;
    }

    public void setCarePlanId(int carePlanId) {
        this.carePlanId = carePlanId;
    }
}
