package com.eldercare.modules.resident_intake.family_contacts.service;

import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactCreateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.request.ResidentContactUpdateRequest;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentContactResponse;
import com.eldercare.modules.resident_intake.family_contacts.dto.response.ResidentQuickContactResponse;

import java.util.List;

public interface ResidentContactService {

        List<ResidentContactResponse> getResidentContacts(Long residentId);

        ResidentContactResponse createResidentContact(Long residentId, ResidentContactCreateRequest request);

        ResidentContactResponse updateResidentContact(
                        Long residentId,
                        Long residentContactId,
                        ResidentContactUpdateRequest request);

        void deleteResidentContact(Long residentId, Long residentContactId);

        ResidentQuickContactResponse getResidentGuarantorContact(Long residentId);

        ResidentQuickContactResponse getResidentPrimaryContact(Long residentId);
}