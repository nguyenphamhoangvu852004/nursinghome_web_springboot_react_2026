package com.eldercare.modules.resident_intake.family_contacts.service.impl;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.AddressEntity;
import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactListResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactListResponseContainer;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.Meta;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentByContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.exception.ContactDeletionBlockedException;
import com.eldercare.modules.resident_intake.family_contacts.exception.ContactEmailAlreadyExistsException;
import com.eldercare.modules.resident_intake.family_contacts.exception.ContactNotFoundException;
import com.eldercare.modules.resident_intake.family_contacts.mapper.ContactMapper;
import com.eldercare.modules.resident_intake.family_contacts.mapper.ResidentContactMapper;
import com.eldercare.modules.resident_intake.family_contacts.service.ContactService;
import com.eldercare.modules.resident_intake.resident.repository.ContactRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentContactRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {

        private static final int DEFAULT_PAGE = 1;
        private static final int DEFAULT_PAGE_SIZE = 10;
        private static final int MAX_PAGE_SIZE = 100;

        private final ContactRepository contactRepository;
        private final ResidentContactRepository residentContactRepository;
        private final ContactMapper contactMapper;
        private final ResidentContactMapper residentContactMapper;
        private final EntityManager entityManager;

        // API 28
        @Override
        @Transactional(readOnly = true)
        public ContactListResponseContainer getContacts(String search, boolean includeDeleted, int page, int pageSize) {

                int normalizedPage = page < 1 ? DEFAULT_PAGE : page;
                int normalizedPageSize = pageSize < 1 ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
                String normalizedSearch = normalizeNullableText(search);

                Pageable pageable = PageRequest.of(
                                normalizedPage - 1,
                                normalizedPageSize,
                                Sort.by(Sort.Direction.DESC, "createdAt"));

                Page<ContactEntity> contactPage = contactRepository.searchContacts(normalizedSearch, includeDeleted,
                                pageable);

                List<ContactListResponse> contacts = contactPage.getContent()
                                .stream()
                                .map(contactMapper::toListResponse)
                                .toList();

                Meta meta = Meta.builder()
                                .total(contactPage.getTotalElements())
                                .page(contactPage.getNumber() + 1)
                                .pageSize(contactPage.getSize())
                                .totalPages(contactPage.getTotalPages())
                                .hasNext(contactPage.hasNext())
                                .hasPrevious(contactPage.hasPrevious())
                                .build();

                return ContactListResponseContainer.builder()
                                .contacts(contacts)
                                .meta(meta)
                                .build();
        }

        // API 29
        @Override
        @Transactional(readOnly = true)
        public ContactResponse getContactById(Long id) {
                return contactMapper.toResponse(findContactById(id));
        }

        // API 30
        @Override
        public ContactResponse createContact(ContactCreateRequest request) {

                String email = normalizeEmail(request.getEmail());
                validateEmailForCreate(email);

                AddressEntity address = resolveAddress(request.getAddressId());
                ContactEntity contact = contactMapper.toEntity(request);

                contact.setFirstName(requireText(request.getFirstName(), "First name"));
                contact.setMiddleName(normalizeNullableText(request.getMiddleName()));
                contact.setLastName(requireText(request.getLastName(), "Last name"));
                contact.setPhonePrimary(requireText(request.getPhonePrimary(), "Primary phone"));
                contact.setPhoneSecondary(normalizeNullableText(request.getPhoneSecondary()));
                contact.setEmail(email);
                contact.setAddress(address);
                contact.setDeleted(false);

                return contactMapper.toResponse(contactRepository.save(contact));
        }

        // API 31
        @Override
        public ContactResponse updateContact(Long id, ContactUpdateRequest request) {

                ContactEntity contact = findContactById(id);

                if (request.getFirstName() != null)
                        contact.setFirstName(requireText(request.getFirstName(), "First name"));

                if (request.getMiddleName() != null)
                        contact.setMiddleName(normalizeNullableText(request.getMiddleName()));

                if (request.getLastName() != null)
                        contact.setLastName(requireText(request.getLastName(), "Last name"));

                if (request.getPhonePrimary() != null)
                        contact.setPhonePrimary(requireText(request.getPhonePrimary(), "Primary phone"));

                if (request.getPhoneSecondary() != null)
                        contact.setPhoneSecondary(normalizeNullableText(request.getPhoneSecondary()));

                if (request.getEmail() != null) {
                        String email = normalizeEmail(request.getEmail());
                        validateEmailForUpdate(email, id);
                        contact.setEmail(email);
                }

                if (request.getAddressId() != null)
                        contact.setAddress(resolveAddress(request.getAddressId()));

                return contactMapper.toResponse(contactRepository.save(contact));
        }

        // API 32
        @Override
        public void deleteContact(Long id) {

                ContactEntity contact = findContactById(id);

                if (contact.isDeleted())
                        return;

                if (residentContactRepository.existsAsGuarantorOfActiveResident(id))
                        throw new ContactDeletionBlockedException(id);

                contact.setDeleted(true);
                contactRepository.save(contact);
        }

        // API 33
        @Override
        @Transactional(readOnly = true)
        public List<ResidentByContactResponse> getResidentsByContact(Long contactId) {

                findContactById(contactId);

                return residentContactRepository.findAllByContactIdWithResident(contactId)
                                .stream()
                                .map(residentContactMapper::toResidentResponse)
                                .toList();
        }

        private ContactEntity findContactById(Long id) {
                if (id == null || id <= 0)
                        throw new ContactNotFoundException(id);
                return contactRepository.findById(id).orElseThrow(() -> new ContactNotFoundException(id));
        }

        private AddressEntity resolveAddress(Long addressId) {
                if (addressId == null)
                        return null;

                AddressEntity address = entityManager.find(AddressEntity.class, addressId);

                if (address == null)
                        throw new EntityNotFoundException("Address not found with id: " + addressId);

                return address;
        }

        private void validateEmailForCreate(String email) {
                if (email != null && contactRepository.existsByEmailIgnoreCase(email))
                        throw new ContactEmailAlreadyExistsException(email);
        }

        private void validateEmailForUpdate(String email, Long contactId) {
                if (email != null && contactRepository.existsByEmailIgnoreCaseAndIdNot(email, contactId))
                        throw new ContactEmailAlreadyExistsException(email);
        }

        private String requireText(String value, String fieldName) {
                if (value == null || value.trim().isEmpty())
                        throw new IllegalArgumentException(fieldName + " must not be blank");

                return value.trim();
        }

        private String normalizeNullableText(String value) {
                if (value == null)
                        return null;
                String normalized = value.trim();
                return normalized.isEmpty() ? null : normalized;
        }

        private String normalizeEmail(String email) {
                String normalized = normalizeNullableText(email);
                return normalized == null ? null : normalized.toLowerCase(Locale.ROOT);
        }
}