package com.eldercare.modules.resident_intake.admission_ledger.dto.request;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionDischargeRequest {
  private LocalDate dischargeDate;
  private String dischargeReason;
}