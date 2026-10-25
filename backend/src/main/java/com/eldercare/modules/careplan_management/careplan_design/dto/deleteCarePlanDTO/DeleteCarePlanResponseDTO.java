package com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteCarePlanResponseDTO {
    @JsonProperty("id")
    public int carePlanId;
    public boolean isDeleted;

    public DeleteCarePlanResponseDTO(int carePlanId, boolean isDeleted) {
        this.carePlanId = carePlanId;
        this.isDeleted = isDeleted;
    }

}
