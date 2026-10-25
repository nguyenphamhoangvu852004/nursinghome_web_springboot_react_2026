package com.eldercare.modules.admin.facility_setup.inventory.category.dto.response;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCategoryResponse {
    
    private long id;

    @JsonProperty("category_name")
    private String categoryName;

    private String description;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;
}
