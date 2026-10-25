package com.eldercare.modules.resident_intake.family_contacts.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentByContactResponse {

    private Long residentId;

    private String residentName;

    private String relationshipType;

    private String residentStatus;

    @JsonProperty("isPrimary")
    private Boolean isPrimary;

    @JsonProperty("isEmergencyContact")
    private Boolean isEmergencyContact;

    @JsonProperty("isGuarantor")
    private Boolean isGuarantor;
}