package com.eldercare.modules.resident_intake.family_contacts.mapper;

import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.ResidentContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentByContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentQuickContactResponse;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ResidentContactMapper {

        public ResidentContactEntity toEntity(
                        ResidentContactCreateRequest request,
                        ResidentEntity resident,
                        ContactEntity contact) {

                if (request == null || resident == null || contact == null)
                        return null;

                return ResidentContactEntity.builder()
                                .resident(resident)
                                .contact(contact)
                                .relationshipType(normalizeRelationshipType(request.getRelationshipType()))
                                .isPrimary(request.isPrimary())
                                .isEmergencyContact(request.isEmergencyContact())
                                .isGuarantor(request.isGuarantor())
                                .financialResponsibilityPct(defaultPercentage(request.getFinancialResponsibilityPct()))
                                .build();
        }

        public ResidentContactResponse toResponse(ResidentContactEntity entity) {
                if (entity == null)
                        return null;

                ContactEntity contact = entity.getContact();
                ResidentEntity resident = entity.getResident();

                return ResidentContactResponse.builder()
                                .residentContactId(entity.getId())
                                .residentId(resident != null ? resident.getId() : null)
                                .contactId(contact != null ? contact.getId() : null)
                                .firstName(contact != null ? contact.getFirstName() : null)
                                .middleName(contact != null ? contact.getMiddleName() : null)
                                .lastName(contact != null ? contact.getLastName() : null)
                                .fullName(buildContactFullName(contact))
                                .phonePrimary(contact != null ? contact.getPhonePrimary() : null)
                                .phoneSecondary(contact != null ? contact.getPhoneSecondary() : null)
                                .email(contact != null ? contact.getEmail() : null)
                                .addressId(contact != null && contact.getAddress() != null
                                                ? contact.getAddress().getId()
                                                : null)
                                .relationshipType(entity.getRelationshipType())
                                .isPrimary(entity.isPrimary())
                                .isEmergencyContact(entity.isEmergencyContact())
                                .isGuarantor(entity.isGuarantor())
                                .financialResponsibilityPct(defaultPercentage(entity.getFinancialResponsibilityPct()))
                                .createdAt(entity.getCreatedAt())
                                .build();
        }

        public ResidentByContactResponse toResidentByContactResponse(ResidentContactEntity entity) {
                if (entity == null || entity.getResident() == null)
                        return null;

                ResidentEntity resident = entity.getResident();

                return ResidentByContactResponse.builder()
                                .residentId(resident.getId())
                                .residentName(buildResidentFullName(resident))
                                .relationshipType(entity.getRelationshipType())
                                .residentStatus(resident.getStatus())
                                .build();
        }

        public void updateEntity(ResidentContactEntity entity, ResidentContactUpdateRequest request) {
                if (entity == null || request == null)
                        return;

                if (request.getRelationshipType() != null)
                        entity.setRelationshipType(normalizeRelationshipType(request.getRelationshipType()));

                if (request.getIsPrimary() != null)
                        entity.setPrimary(request.getIsPrimary());

                if (request.getIsEmergencyContact() != null)
                        entity.setEmergencyContact(request.getIsEmergencyContact());

                if (request.getIsGuarantor() != null)
                        entity.setGuarantor(request.getIsGuarantor());

                if (request.getFinancialResponsibilityPct() != null)
                        entity.setFinancialResponsibilityPct(request.getFinancialResponsibilityPct());
        }

        public String buildContactFullName(ContactEntity contact) {
                if (contact == null)
                        return "";

                return Stream.of(contact.getFirstName(), contact.getMiddleName(), contact.getLastName())
                                .filter(value -> value != null && !value.isBlank())
                                .map(String::trim)
                                .collect(Collectors.joining(" "));
        }

        public String buildResidentFullName(ResidentEntity resident) {
                if (resident == null)
                        return "";

                return Stream.of(resident.getFirstName(), resident.getMiddleName(), resident.getLastName())
                                .filter(value -> value != null && !value.isBlank())
                                .map(String::trim)
                                .collect(Collectors.joining(" "));
        }

        private String normalizeRelationshipType(String relationshipType) {
                if (relationshipType == null)
                        return null;
                return relationshipType.trim().toUpperCase().replace(' ', '_');
        }

        private BigDecimal defaultPercentage(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }

        public ResidentByContactResponse toResidentResponse(ResidentContactEntity entity) {
                ResidentEntity resident = entity.getResident();

                return ResidentByContactResponse.builder()
                                .residentId(resident.getId())
                                .residentName(buildResidentFullName(resident))
                                .relationshipType(entity.getRelationshipType())
                                .residentStatus(resident.getStatus() == null ? null : resident.getStatus().toString())
                                .isPrimary(entity.isPrimary())
                                .isEmergencyContact(entity.isEmergencyContact())
                                .isGuarantor(entity.isGuarantor())
                                .build();
        }

        public ResidentQuickContactResponse toQuickResponse(ResidentContactEntity entity) {
                return ResidentQuickContactResponse.builder()
                                .id(entity.getContact().getId())
                                .firstName(entity.getContact().getFirstName())
                                .isPrimary(entity.isPrimary())
                                .isGuarantor(entity.isGuarantor())
                                .build();
        }
}