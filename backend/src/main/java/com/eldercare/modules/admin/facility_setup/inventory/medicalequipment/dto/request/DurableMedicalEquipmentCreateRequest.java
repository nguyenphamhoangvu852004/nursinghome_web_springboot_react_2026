package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DurableMedicalEquipmentCreateRequest {

    @JsonProperty("item_name")
    @NotBlank(message = "Item name is required")
    private String itemName;

    @JsonProperty("category_id")
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @JsonProperty("asset_tag")
    @NotBlank(message = "Asset tag is required")
    private String assetTag;

    @JsonProperty("facility_id")
    @NotNull(message = "Facility ID is required")
    @Positive(message = "Facility ID must be a positive number")
    private Long facilityId;

    @JsonProperty("unit_value")
    @NotNull(message = "Unit value is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Unit value must be zero or greater")
    private BigDecimal unitValue;

}
