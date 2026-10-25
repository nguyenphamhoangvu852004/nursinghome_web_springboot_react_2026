package com.eldercare.modules.resident_intake.admission_ledger.dto.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionCreateRequest {

  private Long preAdmissionScreeningId;

  private Long facilityId;

  private Long bedId;

  private LocalDate admissionDate;
}
