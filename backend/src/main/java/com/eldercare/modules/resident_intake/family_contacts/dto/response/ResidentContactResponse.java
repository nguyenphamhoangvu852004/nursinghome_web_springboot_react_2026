package com.eldercare.modules.resident_intake.family_contacts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentContactResponse {

    private Long residentContactId;

    private Long residentId;

    private Long contactId;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String phonePrimary;

    private String phoneSecondary;

    private String email;

    private Long addressId;

    private String relationshipType;

    private boolean isPrimary;

    private boolean isEmergencyContact;

    private boolean isGuarantor;

    private BigDecimal financialResponsibilityPct;

    private OffsetDateTime createdAt;
}