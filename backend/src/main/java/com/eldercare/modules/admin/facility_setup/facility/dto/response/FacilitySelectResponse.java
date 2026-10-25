package com.eldercare.modules.admin.facility_setup.facility.dto.response;

import java.util.List;

import com.eldercare.modules.admin.facility_setup.facility.facility_layout.dto.response.RoomSelectResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySelectResponse {

  private Long id;
  private String name;
  private List<RoomSelectResponse> rooms;
}
