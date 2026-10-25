package com.eldercare.modules.resident_intake.family_contacts.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentContactUpdateRequest {

    @Size(min = 1, max = 50, message = "Relationship type must contain between 1 and 50 characters")
    private String relationshipType;

    private Boolean isPrimary;

    private Boolean isEmergencyContact;

    private Boolean isGuarantor;

    @DecimalMin(value = "0.00", inclusive = true, message = "Financial responsibility cannot be less than 0%")
    @DecimalMax(value = "100.00", inclusive = true, message = "Financial responsibility cannot exceed 100%")
    private BigDecimal financialResponsibilityPct;
}