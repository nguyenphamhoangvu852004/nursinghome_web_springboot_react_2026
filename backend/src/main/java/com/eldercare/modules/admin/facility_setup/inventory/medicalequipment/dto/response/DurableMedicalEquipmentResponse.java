package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response;

import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.enums.DurableMedicalEquipmentEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DurableMedicalEquipmentResponse {

    private long id;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("category")
    private InventoryCategoryResponse category;

    @JsonProperty("asset_tag")
    private String assetTag;

    @JsonProperty("status")
    private DurableMedicalEquipmentEnum status;

    @JsonProperty("facility")
    private FacilityResponse facility;

    // @JsonProperty("user")
    // private UserResponse assignedToUser;

    @JsonProperty("unit_value")
    private String unitValue;
}

// class UserResponse {
// @JsonProperty("assigned_to_user")
// private long id;

// @JsonProperty("assigned_to_user_name")
// private String userName;
// }
