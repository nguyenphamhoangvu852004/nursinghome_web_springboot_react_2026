package com.eldercare.modules.resident_intake.family_contacts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactListResponseContainer {

    private List<ContactListResponse> contacts;

    private Meta meta;
}