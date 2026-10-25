package com.eldercare.modules.resident_intake.family_contacts.service.impl;

import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.ResidentContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentQuickContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.exception.ResidentContactNotFoundException;
import com.eldercare.modules.resident_intake.family_contacts.mapper.ResidentContactMapper;
import com.eldercare.modules.resident_intake.family_contacts.service.ResidentContactService;
import com.eldercare.modules.resident_intake.resident.repository.ContactRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentContactRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentContactServiceImpl implements ResidentContactService {

        private static final BigDecimal MAX_FINANCIAL_RESPONSIBILITY = new BigDecimal("100.00");

        private final ResidentRepository residentRepository;
        private final ContactRepository contactRepository;
        private final ResidentContactRepository residentContactRepository;
        private final ResidentContactMapper residentContactMapper;

        // API 34
        @Override
        @Transactional(readOnly = true)
        public List<ResidentContactResponse> getResidentContacts(Long residentId) {

                validatePositiveId(residentId, "Resident ID");

                if (!residentRepository.existsByIdAndIsDeletedFalse(residentId))
                        throw new RuntimeException("Resident not found with id: " + residentId);

                return residentContactRepository.findAllByResidentIdOrderByIsPrimaryDescCreatedAtAsc(residentId)
                                .stream()
                                .map(residentContactMapper::toResponse)
                                .toList();
        }

        // API 35
        @Override
        @Transactional
        public ResidentContactResponse createResidentContact(
                        Long residentId,
                        ResidentContactCreateRequest request) {

                validatePositiveId(residentId, "Resident ID");

                if (request == null)
                        throw new IllegalArgumentException("Request body is required");

                ResidentEntity resident = residentRepository.findByIdAndIsDeletedFalse(residentId)
                                .orElseThrow(() -> new RuntimeException("Resident not found with id: " + residentId));

                ContactEntity contact = contactRepository.findByIdAndIsDeletedFalse(request.getContactId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Contact not found with id: " + request.getContactId()));

                if (residentContactRepository.existsByResidentIdAndContactId(residentId, request.getContactId()))
                        throw new IllegalStateException("This contact is already linked to the resident");

                BigDecimal financialPercentage = normalizePercentage(request.getFinancialResponsibilityPct());

                validateFinancialResponsibilityForCreate(residentId, financialPercentage);

                if (request.isPrimary())
                        residentContactRepository.clearPrimaryContact(residentId, null);

                ResidentContactEntity entity = residentContactMapper.toEntity(request, resident, contact);
                entity.setFinancialResponsibilityPct(financialPercentage);

                return residentContactMapper.toResponse(residentContactRepository.save(entity));
        }

        // API 36
        @Override
        @Transactional
        public ResidentContactResponse updateResidentContact(
                        Long residentId,
                        Long residentContactId,
                        ResidentContactUpdateRequest request) {

                validatePositiveId(residentId, "Resident ID");
                validatePositiveId(residentContactId, "Resident Contact ID");

                if (request == null)
                        throw new IllegalArgumentException("Request body is required");

                if (!residentRepository.existsByIdAndIsDeletedFalse(residentId))
                        throw new RuntimeException("Resident not found with id: " + residentId);

                ResidentContactEntity entity = residentContactRepository
                                .findByIdAndResidentId(residentContactId, residentId)
                                .orElseThrow(() -> new RuntimeException("Resident contact relationship not found"));

                if (request.getFinancialResponsibilityPct() != null) {
                        BigDecimal percentage = normalizePercentage(request.getFinancialResponsibilityPct());
                        validateFinancialResponsibilityForUpdate(residentId, residentContactId, percentage);
                        entity.setFinancialResponsibilityPct(percentage);
                }

                if (request.getRelationshipType() != null) {
                        String relationshipType = request.getRelationshipType().trim();

                        if (relationshipType.isBlank())
                                throw new IllegalArgumentException("Relationship type cannot be blank");

                        entity.setRelationshipType(normalizeRelationshipType(relationshipType));
                }

                if (request.getIsEmergencyContact() != null)
                        entity.setEmergencyContact(request.getIsEmergencyContact());

                if (request.getIsGuarantor() != null)
                        entity.setGuarantor(request.getIsGuarantor());

                if (Boolean.TRUE.equals(request.getIsPrimary())) {
                        residentContactRepository.clearPrimaryContact(residentId, residentContactId);
                        entity.setPrimary(true);
                } else if (Boolean.FALSE.equals(request.getIsPrimary())) {
                        entity.setPrimary(false);
                }

                return residentContactMapper.toResponse(residentContactRepository.save(entity));
        }

        // API 37
        @Override
        @Transactional
        public void deleteResidentContact(Long residentId, Long residentContactId) {

                validatePositiveId(residentId, "Resident ID");
                validatePositiveId(residentContactId, "Resident Contact ID");

                if (!residentRepository.existsByIdAndIsDeletedFalse(residentId))
                        throw new RuntimeException("Resident not found with id: " + residentId);

                ResidentContactEntity entity = residentContactRepository
                                .findByIdAndResidentId(residentContactId, residentId)
                                .orElseThrow(() -> new RuntimeException("Resident contact relationship not found"));

                residentContactRepository.delete(entity);
        }

        private void validateFinancialResponsibilityForCreate(Long residentId, BigDecimal newPercentage) {

                BigDecimal currentTotal = defaultZero(residentContactRepository.sumFinancialResponsibility(residentId));
                BigDecimal newTotal = currentTotal.add(newPercentage);

                validateMaximumFinancialResponsibility(currentTotal, newTotal);
        }

        private void validateFinancialResponsibilityForUpdate(
                        Long residentId,
                        Long residentContactId,
                        BigDecimal updatedPercentage) {

                BigDecimal otherContactsTotal = defaultZero(
                                residentContactRepository.sumFinancialResponsibilityExcluding(
                                                residentId,
                                                residentContactId));

                BigDecimal newTotal = otherContactsTotal.add(updatedPercentage);

                validateMaximumFinancialResponsibility(otherContactsTotal, newTotal);
        }

        private void validateMaximumFinancialResponsibility(
                        BigDecimal currentTotal,
                        BigDecimal finalTotal) {

                if (finalTotal.compareTo(MAX_FINANCIAL_RESPONSIBILITY) > 0) {
                        BigDecimal remaining = MAX_FINANCIAL_RESPONSIBILITY.subtract(currentTotal);

                        throw new IllegalStateException(
                                        "Total financial responsibility cannot exceed 100%. Remaining percentage: "
                                                        + remaining.max(BigDecimal.ZERO));
                }
        }

        private BigDecimal normalizePercentage(BigDecimal value) {

                BigDecimal normalized = value == null ? BigDecimal.ZERO : value;

                if (normalized.compareTo(BigDecimal.ZERO) < 0)
                        throw new IllegalArgumentException("Financial responsibility cannot be negative");

                if (normalized.compareTo(MAX_FINANCIAL_RESPONSIBILITY) > 0)
                        throw new IllegalArgumentException("Financial responsibility cannot exceed 100%");

                return normalized;
        }

        private BigDecimal defaultZero(BigDecimal value) {
                return value == null ? BigDecimal.ZERO : value;
        }

        private String normalizeRelationshipType(String relationshipType) {
                return relationshipType.trim().toUpperCase().replace(' ', '_');
        }

        private void validatePositiveId(Long id, String fieldName) {
                if (id == null || id <= 0)
                        throw new IllegalArgumentException(fieldName + " must be greater than 0");
        }

        @Override
        @Transactional(readOnly = true)
        public ResidentQuickContactResponse getResidentGuarantorContact(Long residentId) {

                ResidentContactEntity entity = residentContactRepository
                                .findFirstByResident_IdAndIsGuarantorTrueAndContact_IsDeletedFalseOrderByIdAsc(
                                                residentId)
                                .orElseThrow(() -> new ResidentContactNotFoundException(
                                                "No guarantor contact found for resident id: " + residentId));

                return residentContactMapper.toQuickResponse(entity);
        }

        @Override
        @Transactional(readOnly = true)
        public ResidentQuickContactResponse getResidentPrimaryContact(Long residentId) {

                ResidentContactEntity entity = residentContactRepository
                                .findFirstByResident_IdAndIsPrimaryTrueAndContact_IsDeletedFalseOrderByIdAsc(residentId)
                                .orElseThrow(() -> new ResidentContactNotFoundException(
                                                "No primary contact found for resident id: " + residentId));

                return residentContactMapper.toQuickResponse(entity);
        }
}