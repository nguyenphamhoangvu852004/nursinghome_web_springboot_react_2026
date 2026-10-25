package com.eldercare.modules.resident_intake.family_contacts.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String fullName;

    private String phonePrimary;

    private String phoneSecondary;

    private String email;

    private Long addressId;

    private String streetLine1;

    private String streetLine2;

    private String city;

    private String state;

    private String zipCode;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}