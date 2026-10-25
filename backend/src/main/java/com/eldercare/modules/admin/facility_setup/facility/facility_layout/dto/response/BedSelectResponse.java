package com.eldercare.modules.admin.facility_setup.facility.facility_layout.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BedSelectResponse {

  private Long id;
  private String bedNumber;
}
