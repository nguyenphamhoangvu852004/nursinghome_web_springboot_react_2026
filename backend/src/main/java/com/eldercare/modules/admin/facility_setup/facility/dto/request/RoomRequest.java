package com.eldercare.modules.admin.facility_setup.facility.dto.request;

import com.eldercare.common.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RoomRequest {
    @JsonProperty("room_number")
    private String roomNumber;
    
    @JsonProperty("room_type")
    private RoomType roomType;
    
    @JsonProperty("facility_id")
    private Long facilityId; // Used for creation
}
