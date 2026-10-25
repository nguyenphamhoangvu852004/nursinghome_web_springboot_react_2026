package com.eldercare.modules.resident_intake.admission_ledger.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eldercare.common.enums.BedStatus;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.BedRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionCreateRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionDischargeRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionResponse;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionSelectDTO;
import com.eldercare.modules.resident_intake.admission_ledger.repository.AdmissionRepository;
import com.eldercare.modules.resident_intake.admission_ledger.service.AdmissionService;
import com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity;
import com.eldercare.modules.resident_intake.pre_admission.repository.PreAdmissionScreeningRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdmissionServiceImpl implements AdmissionService {

  private final AdmissionRepository admRepo;
  private final PreAdmissionScreeningRepository preRepo;
  private final ResidentRepository residentRepo;
  private final BedRepository bedRepository;

  @Override
  @Transactional
  public AdmissionResponse create(AdmissionCreateRequest req) {

    PreAdmissionScreeningEntity pre = preRepo.findById(req.getPreAdmissionScreeningId())
        .orElseThrow(() -> new RuntimeException("Pre-admission screening not found"));

    if (!"COMPLETED".equals(pre.getStatus()) || !Boolean.TRUE.equals(pre.getIsCurrent())) {
      throw new RuntimeException("Pre-admission screening is not eligible for admission.");
    }

    BedEntity bed = bedRepository.findById(req.getBedId())
        .orElseThrow(() -> new RuntimeException("Bed not found"));

    if (bed.getStatus() != BedStatus.AVAILABLE) {
      throw new RuntimeException("Selected bed is not available.");
    }

    if (!bed.getRoom().getFacility().getId().equals(req.getFacilityId())) {
      throw new RuntimeException("Bed does not belong to selected facility.");
    }

    Long residentId = pre.getResident().getId();

    admRepo.findByResidentIdAndIsCurrentTrue(residentId)
        .ifPresent(old -> {
          old.setIsCurrent(false);
          admRepo.save(old);
        });

    AdmissionEntity admission = new AdmissionEntity();

    admission.setResident(pre.getResident());
    admission.setPreAdmissionScreening(pre);
    admission.setFacility(bed.getRoom().getFacility());

    admission.setAdmissionDate(req.getAdmissionDate());
    admission.setIsCurrent(true);
    admission.setCreatedAt(OffsetDateTime.now());

    admRepo.save(admission);

    bed.setStatus(BedStatus.OCCUPIED);
    bedRepository.save(bed);

    ResidentEntity resident = pre.getResident();
    resident.setStatus("ACTIVE");
    resident.setBed(bed);
    residentRepo.save(resident);

    return toResponse(admission);
  }

  @Override
  public Page<AdmissionResponse> listPaged(Pageable pageable) {
    return admRepo.findAll(pageable).map(this::toResponse);
  }

  @Override
  @Transactional
  public AdmissionResponse discharge(Long id, AdmissionDischargeRequest req) {
    AdmissionEntity adm = admRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));

    adm.setDischargeDate(req.getDischargeDate());
    adm.setDischargeReason(req.getDischargeReason());
    adm.setIsCurrent(false);

    admRepo.save(adm);

    ResidentEntity r = adm.getResident();
    BedEntity bed = r.getBed();
    if (bed != null) {
      bed.setStatus(BedStatus.AVAILABLE);
      r.setBed(null);

      bedRepository.save(bed);
    }
    r.setStatus("DISCHARGED");

    residentRepo.save(r);
    bedRepository.save(bed);

    return toResponse(adm);
  }

  @Override
  public List<AdmissionSelectDTO> listActiveForSelect() {
    return admRepo.findByIsCurrentTrueAndDischargeDateIsNull().stream().map(a -> {
      AdmissionSelectDTO dto = new AdmissionSelectDTO();
      dto.setId(a.getId());
      dto.setResidentName(a.getResident().getFirstName() + " " + a.getResident().getLastName());
      return dto;
    }).toList();
  }

  private AdmissionResponse toResponse(AdmissionEntity a) {
    AdmissionResponse dto = new AdmissionResponse();
    dto.setId(a.getId());
    dto.setAdmissionDate(a.getAdmissionDate());
    dto.setResidentId(a.getResident().getId());
    dto.setResidentName(a.getResident().getFirstName() + " " + a.getResident().getLastName());
    dto.setFacilityId(a.getFacility().getId());
    dto.setPreAdmissionScreeningId(a.getPreAdmissionScreening().getId());
    dto.setDischargeDate(a.getDischargeDate());
    dto.setDischargeReason(a.getDischargeReason());
    dto.setStatus(a.getDischargeDate() == null ? "ACTIVE" : "DISCHARGED");
    return dto;
  }
}