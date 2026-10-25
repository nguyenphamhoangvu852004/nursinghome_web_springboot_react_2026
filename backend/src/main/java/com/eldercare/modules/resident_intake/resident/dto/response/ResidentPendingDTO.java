package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResidentPendingDTO {
  private Long id;
  private String fullName;
  private String status;
}
