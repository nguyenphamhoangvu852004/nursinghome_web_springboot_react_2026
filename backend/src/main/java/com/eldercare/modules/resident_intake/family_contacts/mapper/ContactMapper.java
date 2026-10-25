package com.eldercare.modules.resident_intake.family_contacts.mapper;

import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactListResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ContactMapper {

    public ContactEntity toEntity(ContactCreateRequest request) {
        if (request == null)
            return null;

        return ContactEntity.builder()
                .firstName(normalizeRequiredText(request.getFirstName()))
                .middleName(normalizeNullableText(request.getMiddleName()))
                .lastName(normalizeRequiredText(request.getLastName()))
                .phonePrimary(normalizeRequiredText(request.getPhonePrimary()))
                .phoneSecondary(normalizeNullableText(request.getPhoneSecondary()))
                .email(normalizeEmail(request.getEmail()))
                .isDeleted(false)
                .build();
    }

    public ContactListResponse toListResponse(ContactEntity entity) {
        if (entity == null)
            return null;

        return ContactListResponse.builder()
                .id(entity.getId())
                .fullName(buildFullName(entity))
                .phonePrimary(entity.getPhonePrimary())
                .email(entity.getEmail())
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ContactResponse toResponse(ContactEntity entity) {
        if (entity == null)
            return null;

        return ContactResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .middleName(entity.getMiddleName())
                .lastName(entity.getLastName())
                .fullName(buildFullName(entity))
                .phonePrimary(entity.getPhonePrimary())
                .phoneSecondary(entity.getPhoneSecondary())
                .email(entity.getEmail())
                .addressId(entity.getAddress() != null ? entity.getAddress().getId() : null)
                .isDeleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(ContactEntity entity, ContactUpdateRequest request) {
        if (entity == null || request == null)
            return;

        if (request.getFirstName() != null)
            entity.setFirstName(normalizeRequiredText(request.getFirstName()));

        if (request.getMiddleName() != null)
            entity.setMiddleName(normalizeNullableText(request.getMiddleName()));

        if (request.getLastName() != null)
            entity.setLastName(normalizeRequiredText(request.getLastName()));

        if (request.getPhonePrimary() != null)
            entity.setPhonePrimary(normalizeRequiredText(request.getPhonePrimary()));

        if (request.getPhoneSecondary() != null)
            entity.setPhoneSecondary(normalizeNullableText(request.getPhoneSecondary()));

        if (request.getEmail() != null)
            entity.setEmail(normalizeEmail(request.getEmail()));
    }

    public String buildFullName(ContactEntity entity) {
        if (entity == null)
            return "";

        return Stream.of(entity.getFirstName(), entity.getMiddleName(), entity.getLastName())
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));
    }

    private String normalizeRequiredText(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullableText(String value) {
        if (value == null)
            return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeEmail(String email) {
        if (email == null)
            return null;
        String normalized = email.trim().toLowerCase();
        return normalized.isEmpty() ? null : normalized;
    }
}