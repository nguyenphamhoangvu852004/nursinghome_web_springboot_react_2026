package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DurableMedicalEquipmentChangeStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
}
