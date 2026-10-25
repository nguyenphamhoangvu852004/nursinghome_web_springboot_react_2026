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
public class ContactListResponse {

    private Long id;

    private String fullName;

    private String phonePrimary;

    private String email;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    private OffsetDateTime createdAt;
}