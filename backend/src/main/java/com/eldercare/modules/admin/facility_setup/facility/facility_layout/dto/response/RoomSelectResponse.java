package com.eldercare.modules.admin.facility_setup.facility.facility_layout.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomSelectResponse {

    private Long id;
    private String roomNumber;
    private List<BedSelectResponse> beds;
}
