package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InventoryCategoryResponse {

    @JsonProperty("category_id")
    private Long id;
    @JsonProperty("category_name")
    private String categoryName;
}
