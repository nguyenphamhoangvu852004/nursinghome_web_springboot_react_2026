package com.eldercare.modules.resident_intake.pre_admission.dto.response;

import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreResponse {
  private Long id;
  private String status;
  private Long residentId;
  private String residentName;
  private OffsetDateTime createdAt;
}
