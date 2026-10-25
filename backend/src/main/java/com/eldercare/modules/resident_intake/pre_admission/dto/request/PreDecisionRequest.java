package com.eldercare.modules.resident_intake.pre_admission.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreDecisionRequest {
  private String status; // COMPLETED or REJECTED
}