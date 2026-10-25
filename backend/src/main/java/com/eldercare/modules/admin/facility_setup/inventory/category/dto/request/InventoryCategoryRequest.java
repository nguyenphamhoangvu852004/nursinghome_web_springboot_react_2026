package com.eldercare.modules.admin.facility_setup.inventory.category.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InventoryCategoryRequest {
    
    @NotBlank(message = "Category name is required")
    @JsonProperty("category_name")
    private String categoryName;

    @NotBlank(message = "Description is required")
    private String description;
}
