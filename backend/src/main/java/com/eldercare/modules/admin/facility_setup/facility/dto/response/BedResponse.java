package com.eldercare.modules.admin.facility_setup.facility.dto.response;

import com.eldercare.common.enums.BedStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BedResponse {
    private Long id;

    @JsonProperty("bed_number")
    private String bedNumber;

    private BedStatus status;

    @JsonProperty("room_id")
    private Long roomId;

    @JsonProperty("resident_name")
    private String residentName;

    @JsonProperty("admission_date")
    private LocalDate admissionDate;
}
