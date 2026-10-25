package com.eldercare.modules.admin.facility_setup.facility.dto.request;

import com.eldercare.common.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressDto {
    @JsonProperty("street_line1")
    private String streetLine1;
    
    @JsonProperty("street_line2")
    private String streetLine2;
    
    private String city;
    private String state;
    
    @JsonProperty("zip_code")
    private String zipCode;
    
    @JsonProperty("address_type")
    private AddressType addressType;
}
