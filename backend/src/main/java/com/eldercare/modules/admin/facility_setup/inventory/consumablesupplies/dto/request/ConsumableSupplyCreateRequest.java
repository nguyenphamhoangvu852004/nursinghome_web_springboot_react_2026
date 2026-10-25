package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request;

import java.math.BigDecimal;

import org.springframework.security.core.parameters.P;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class ConsumableSupplyCreateRequest {
    
    @JsonProperty("item_name")
    @NotBlank(message = "Item name is required")
    private String itemName;

    @JsonProperty("category_id")
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @JsonProperty("facility_id")
    @NotNull(message = "Facility ID is required")
    @Positive(message = "Facility ID must be a positive number")
    private Long facilityId;

    @JsonProperty("stock_on_hand")
    @NotNull(message = "Initial stock is required")
    @Positive(message = "Initial stock must be a positive number")
    private int stockOnHand;

    @JsonProperty("reorder_threshold")
    @NotNull(message = "Reorder threshold is required")
    @Positive(message = "Reorder threshold must be a positive number")
    private int reorderThreshold;

    @JsonProperty("unit_cost")
    @NotNull(message = "Unit cost is required")
    @Positive(message = "Unit cost must be a positive number")
    private BigDecimal unitCost;

    @JsonProperty("private_pay_rate")
    @NotNull(message = "Private pay rate is required")
    @Positive(message = "Private pay rate must be a positive number")
    private BigDecimal privatePayRate;
}
