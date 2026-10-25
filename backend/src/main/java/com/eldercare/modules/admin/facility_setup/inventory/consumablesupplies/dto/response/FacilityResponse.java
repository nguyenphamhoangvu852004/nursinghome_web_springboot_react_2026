package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FacilityResponse {
    @JsonProperty("facility_id")
    private long id;

    @JsonProperty("facility_name")
    private String name;
}