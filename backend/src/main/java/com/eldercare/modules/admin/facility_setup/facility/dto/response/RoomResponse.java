package com.eldercare.modules.admin.facility_setup.facility.dto.response;

import com.eldercare.common.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoomResponse {
    private Long id;

    @JsonProperty("room_number")
    private String roomNumber;

    @JsonProperty("room_type")
    private RoomType roomType;

    @JsonProperty("facility_id")
    private Long facilityId;

    @JsonProperty("facility_name")
    private String facilityName;

    private List<BedResponse> beds;

    private int capacity;

    @JsonProperty("occupied_count")
    private int occupiedCount;

    private String status;
}
