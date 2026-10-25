package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response;

import java.math.BigDecimal;

import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.enums.ConsumableSupplyEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ConsumableSupplyResponse {

    private long id;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("category")
    private InventoryCategoryResponse category;

    @JsonProperty("facility")
    private FacilityResponse facility;

    @JsonProperty("stock_on_hand")
    private int stockOnHand;

    private int total;

    @JsonProperty("reorder_threshold")
    private BigDecimal reorderThreshold;

    @JsonProperty("unit_cost")
    private BigDecimal unitCost;

    @JsonProperty("private_pay_rate")
    private BigDecimal privatePayRate;

    private ConsumableSupplyEnum status;

}
