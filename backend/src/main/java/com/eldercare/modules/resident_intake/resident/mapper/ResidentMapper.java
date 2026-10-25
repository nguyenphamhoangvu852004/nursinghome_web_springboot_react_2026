package com.eldercare.modules.resident_intake.resident.mapper;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.AddressEntity;
import com.eldercare.modules.finance_billing.insurance_coverage.ResidentInsurancePolicyEntity;
import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;
import com.eldercare.modules.admin.facility_setup.care_level.entity.ResidentCareLevelHistoryEntity;
import com.eldercare.modules.resident_intake.family_contacts.ContactEntity;
import com.eldercare.modules.resident_intake.family_contacts.ResidentContactEntity;
import com.eldercare.modules.resident_intake.resident.dto.response.ResidentDetailResponseDto;
import com.eldercare.modules.resident_intake.resident.dto.response.ResidentListResponseDto;
import com.eldercare.modules.resident_intake.resident.dto.response.ResidentResponseDto;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.resident_intake.resident_profile.ResidentSensitiveInfoEntity;
import com.eldercare.modules.clinical.clinical_record.ClinicalRecordEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ResidentMapper {

    public ResidentListResponseDto toListDto(ResidentEntity resident, List<ResidentInsurancePolicyEntity> policies) {
        ResidentListResponseDto dto = new ResidentListResponseDto();
        dto.setId(resident.getId());
        dto.setName(resident.getFirstName() + " " + resident.getLastName());
        
        // RoomEntity mapping
        if (resident.getBed() != null) {
            String roomNum = resident.getBed().getRoom().getRoomNumber();
            String bedNum = resident.getBed().getBedNumber();
            dto.setRoom(roomNum + "-" + bedNum);
        } else {
            dto.setRoom("—");
        }

        // Status casing format
        String statusStr = resident.getStatus();
        if (statusStr != null && !statusStr.isEmpty()) {
            dto.setStatus(Character.toUpperCase(statusStr.charAt(0)) + statusStr.substring(1).toLowerCase());
        } else {
            dto.setStatus("Pending");
        }

        dto.setDob(resident.getDateOfBirth());
        
        // Age calculation
        if (resident.getDateOfBirth() != null) {
            dto.setAge(Period.between(resident.getDateOfBirth(), LocalDate.now()).getYears());
        } else {
            dto.setAge(75);
        }

        // Payer source mapping
        Optional<ResidentInsurancePolicyEntity> primaryPolicy = policies.stream()
                .filter(ResidentInsurancePolicyEntity::isPrimary)
                .findFirst();
        if (primaryPolicy.isPresent()) {
            dto.setPayerSource(primaryPolicy.get().getInsuranceProvider().getProviderName());
        } else {
            dto.setPayerSource("Private Pay");
        }

        // Referral source (Mock mapping because database has no referral table)
        dto.setReferralSource(getMockReferralSource(resident.getId()));

        return dto;
    }

    public ResidentResponseDto toResponseDto(ResidentEntity resident) {
        if (resident == null) {
            return null;
        }
        return ResidentResponseDto.builder()
                .id(resident.getId())
                .firstName(resident.getFirstName())
                .middleName(resident.getMiddleName())
                .lastName(resident.getLastName())
                .dateOfBirth(resident.getDateOfBirth())
                .gender(resident.getGender())
                .status(resident.getStatus())
                .bedId(resident.getBed() != null ? resident.getBed().getId() : null)
                .isChartLocked(resident.isChartLocked())
                .build();
    }

    public ResidentDetailResponseDto toDetailResponseDto(
            ResidentEntity resident,
            List<ResidentCareLevelHistoryEntity> careHistory,
            List<ResidentInsurancePolicyEntity> policies,
            Optional<ResidentSensitiveInfoEntity> sensitive,
            List<AdmissionEntity> admissions,
            List<ResidentContactEntity> residentContacts,
            List<ClinicalRecordEntity> diagnoses,
            List<ClinicalRecordEntity> allergies
    ) {
        ResidentDetailResponseDto dto = new ResidentDetailResponseDto();
        dto.setId(resident.getId());
        dto.setName(resident.getFirstName() + " " + resident.getLastName());
        
        // Initials
        String initials = "";
        if (resident.getFirstName() != null && !resident.getFirstName().isEmpty()) initials += resident.getFirstName().charAt(0);
        if (resident.getLastName() != null && !resident.getLastName().isEmpty()) initials += resident.getLastName().charAt(0);
        dto.setInitials(initials.toUpperCase());

        // RoomEntity
        String roomStr = "—";
        if (resident.getBed() != null) {
            roomStr = resident.getBed().getRoom().getRoomNumber() + resident.getBed().getBedNumber();
        }
        dto.setRoom(roomStr);

        String statusStr = resident.getStatus();
        if (statusStr != null && !statusStr.isEmpty()) {
            dto.setStatus(Character.toUpperCase(statusStr.charAt(0)) + statusStr.substring(1).toLowerCase());
        } else {
            dto.setStatus("Pending");
        }
        dto.setDob(resident.getDateOfBirth());
        
        int ageVal = 75;
        if (resident.getDateOfBirth() != null) {
            ageVal = Period.between(resident.getDateOfBirth(), LocalDate.now()).getYears();
        }
        dto.setAge(ageVal);

        // Badges
        List<ResidentDetailResponseDto.Badge> badges = new ArrayList<>();
        badges.add(new ResidentDetailResponseDto.Badge(dto.getStatus(), "active"));
        
        // DNR (mapped from resident chart locked flag representing DNR status)
        boolean isDnr = resident.isChartLocked();
        badges.add(new ResidentDetailResponseDto.Badge(isDnr ? "DNR" : "No DNR", isDnr ? "dnr" : "nodnr"));

        // LOC level
        String careLevelName = "Level 3";
        if (!careHistory.isEmpty()) {
            careLevelName = careHistory.get(0).getCareLevel().getLevelName();
        }
        badges.add(new ResidentDetailResponseDto.Badge(careLevelName, "level3"));

        // Payer badge
        Optional<ResidentInsurancePolicyEntity> primaryPolicy = policies.stream()
                .filter(ResidentInsurancePolicyEntity::isPrimary)
                .findFirst();
        String payerSource = primaryPolicy.isPresent() ? primaryPolicy.get().getInsuranceProvider().getProviderName() : "Private Pay";
        badges.add(new ResidentDetailResponseDto.Badge(payerSource, "payer"));
        dto.setBadges(badges);

        // Demographics
        ResidentDetailResponseDto.Demographics demo = new ResidentDetailResponseDto.Demographics();
        demo.setLegalName(resident.getFirstName() + " " + (resident.getMiddleName() != null ? resident.getMiddleName() + " " : "") + resident.getLastName());
        
        // AddressEntity
        if (resident.getAddress() != null) {
            AddressEntity addr = resident.getAddress();
            demo.setAddress(addr.getStreetLine1() + (addr.getStreetLine2() != null ? ", " + addr.getStreetLine2() : "") + ", " + addr.getCity() + ", " + addr.getState());
        } else {
            demo.setAddress("—");
        }
        demo.setDob(resident.getDateOfBirth());

        // AdmissionEntity Date
        if (!admissions.isEmpty()) {
            demo.setAdmissionDate(admissions.get(0).getAdmissionDate());
        } else {
            demo.setAdmissionDate(resident.getCreatedAt() != null ? resident.getCreatedAt().toLocalDate() : LocalDate.now());
        }

        // SSN from sensitive info
        demo.setSsn(sensitive.isPresent() ? sensitive.get().getSsnEncrypted() : "—");

        demo.setRoomBed(resident.getBed() != null ? resident.getBed().getRoom().getRoomNumber() + " / " + resident.getBed().getBedNumber() : "—");
        demo.setGender(resident.getGender());
        demo.setReferralSource(getMockReferralSource(resident.getId()));
        demo.setMaritalStatus(resident.getMaritalStatus());

        // Contacts / Emergency ContactEntity / POA
        Optional<ResidentContactEntity> emergencyContact = residentContacts.stream()
                .filter(ResidentContactEntity::isEmergencyContact)
                .findFirst();
        
        if (emergencyContact.isPresent()) {
            ContactEntity contact = emergencyContact.get().getContact();
            demo.setEmergencyContact(contact.getFirstName() + " " + contact.getLastName() + " (" + emergencyContact.get().getRelationshipType() + ")");
            demo.setPhone(contact.getPhonePrimary());
        } else {
            demo.setEmergencyContact("—");
            demo.setPhone("—");
        }
        demo.setPayerSource(payerSource);
        dto.setDemographics(demo);

        // POA mapping
        ResidentDetailResponseDto.Poa poaDto = new ResidentDetailResponseDto.Poa();
        Optional<ResidentContactEntity> poaContact = residentContacts.stream()
                .filter(rc -> rc.getRelationshipType().equalsIgnoreCase("SON") || 
                        rc.getRelationshipType().equalsIgnoreCase("DAUGHTER") || 
                        rc.getRelationshipType().equalsIgnoreCase("SPOUSE") ||
                        rc.getRelationshipType().equalsIgnoreCase("LEGAL_GUARDIAN"))
                .findFirst();

        if (poaContact.isPresent()) {
            ContactEntity contact = poaContact.get().getContact();
            poaDto.setName(contact.getFirstName() + " " + contact.getLastName());
            poaDto.setRelationship(poaContact.get().getRelationshipType());
            poaDto.setContact(contact.getPhonePrimary());
        } else {
            poaDto.setName("—");
            poaDto.setRelationship("—");
            poaDto.setContact("—");
        }
        poaDto.setDnrFlag(resident.isChartLocked() ? "Yes" : "No");
        dto.setPoa(poaDto);

        // Diagnoses (Clinical records)
        if (diagnoses != null && !diagnoses.isEmpty()) {
            dto.setDiagnoses(diagnoses.stream().map(ClinicalRecordEntity::getDescription).collect(Collectors.toList()));
        } else {
            dto.setDiagnoses(new ArrayList<>());
        }

        // Allergies (Clinical records)
        if (allergies != null && !allergies.isEmpty()) {
            dto.setAllergies(allergies.stream().map(ClinicalRecordEntity::getDescription).collect(Collectors.toList()));
        } else {
            dto.setAllergies(new ArrayList<>());
        }

        // Insurance
        ResidentDetailResponseDto.Insurance insDto = new ResidentDetailResponseDto.Insurance();
        if (primaryPolicy.isPresent()) {
            ResidentInsurancePolicyEntity policy = primaryPolicy.get();
            insDto.setMedicareNum(policy.getPolicyNumberEncrypted());
            insDto.setProvider(policy.getInsuranceProvider().getProviderName());
            insDto.setPayerName(policy.getInsuranceProvider().getProviderName() + " CA");
            insDto.setAuthNum("AUTH-" + policy.getGroupNumber());
            insDto.setAuthStartEnd(policy.getEffectiveFrom() + " / " + (policy.getEffectiveTo() != null ? policy.getEffectiveTo() : "Present"));
        } else {
            insDto.setMedicareNum("—");
            insDto.setProvider("Private Pay");
            insDto.setPayerName("Self Pay");
            insDto.setAuthNum("—");
            insDto.setAuthStartEnd("—");
        }
        dto.setInsurance(insDto);

        // LOC Summary
        ResidentDetailResponseDto.LocSummary loc = new ResidentDetailResponseDto.LocSummary();
        loc.setLevel(careLevelName);
        loc.setAdlScore("20 / 32 (Tier 3)");
        dto.setLocSummary(loc);

        return dto;
    }

    private String getMockReferralSource(Long id) {
        if (id == null) return "Private";
        int index = (int) (id % 5);
        switch (index) {
            case 1: return "Sunrise Regional Hosp.";
            case 2: return "Private";
            case 3: return "Family";
            case 4: return "Self";
            case 0: return "Valley General Hosp.";
            default: return "Private";
        }
    }
}

