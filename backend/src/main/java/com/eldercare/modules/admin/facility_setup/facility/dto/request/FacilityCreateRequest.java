package com.eldercare.modules.admin.facility_setup.facility.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FacilityCreateRequest {
    @JsonProperty("facility_code")
    private String facilityCode;
    
    private String name;
    
    @JsonProperty("license_number")
    private String licenseNumber;
    
    @JsonProperty("target_state")
    private String targetState;
    
    private AddressDto address;
    
    @JsonProperty("phone_number")
    private String phoneNumber;
}
