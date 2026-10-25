package com.eldercare.modules.resident_intake.resident.dto.request;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.AddressEntity;
import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ResidentSaveRequestDto {
    // Personal Info
    private String firstName;
    private String lastName;
    private String status;
    private LocalDate dob;
    private String gender;
    private String referralSource;
    private String ssn;
    private String maritalStatus;
    private String referringFacility;

    // ContactEntity & AddressEntity
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;

    // POA
    private boolean poaOnFile;
    private String poaName;
    private String poaRelationship;

    // Insurance
    private String payerSource;
    private String payerType;
    private String medicareNum;
    private String insuranceProvider;
    private String authStartDate;
    private String authEndDate;

    // DNR
    private boolean dnrActive;
}
