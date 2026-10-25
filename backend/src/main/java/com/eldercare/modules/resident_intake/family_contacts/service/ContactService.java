package com.eldercare.modules.resident_intake.family_contacts.service;

import com.eldercare.modules.resident_intake.family_contacts.dto.request.*;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.*;

import java.util.List;

public interface ContactService {

        ContactListResponseContainer getContacts(String search, boolean includeDeleted, int page, int pageSize);

        ContactResponse getContactById(Long id);

        ContactResponse createContact(ContactCreateRequest dto);

        ContactResponse updateContact(Long id, ContactUpdateRequest dto);

        void deleteContact(Long id);

        List<ResidentByContactResponse> getResidentsByContact(Long contactId);
}