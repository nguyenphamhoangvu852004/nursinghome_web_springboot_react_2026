package com.eldercare.modules.resident_intake.admission_ledger.dto.response;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionResponse {
  private Long id;
  private LocalDate admissionDate;
  private Long residentId;
  private String residentName;
  private Long facilityId;
  private Long preAdmissionScreeningId;
  private LocalDate dischargeDate;
  private String dischargeReason;
  private String status;
}