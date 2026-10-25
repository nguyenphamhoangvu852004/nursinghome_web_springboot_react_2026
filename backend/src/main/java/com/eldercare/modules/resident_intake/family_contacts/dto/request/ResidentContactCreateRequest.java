package com.eldercare.modules.resident_intake.family_contacts.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ResidentContactCreateRequest {

    @NotNull(message = "Contact ID is required")
    private Long contactId;

    @NotBlank(message = "Relationship type is required")
    @Size(max = 50, message = "Relationship type must not exceed 50 characters")
    private String relationshipType;

    @Builder.Default
    private boolean isPrimary = false;

    @Builder.Default
    private boolean isEmergencyContact = false;

    @Builder.Default
    private boolean isGuarantor = false;

    @DecimalMin(value = "0.00", inclusive = true, message = "Financial responsibility cannot be less than 0%")
    @DecimalMax(value = "100.00", inclusive = true, message = "Financial responsibility cannot exceed 100%")
    @Builder.Default
    private BigDecimal financialResponsibilityPct = BigDecimal.ZERO;
}