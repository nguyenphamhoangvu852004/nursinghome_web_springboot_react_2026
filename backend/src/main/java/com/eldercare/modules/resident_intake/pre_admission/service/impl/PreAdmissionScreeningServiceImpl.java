package com.eldercare.modules.resident_intake.pre_admission.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreCreateRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreDecisionRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreResponse;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreSelectDTO;
import com.eldercare.modules.resident_intake.pre_admission.repository.PreAdmissionScreeningRepository;
import com.eldercare.modules.resident_intake.pre_admission.service.PreAdmissionScreeningService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreAdmissionScreeningServiceImpl implements PreAdmissionScreeningService {
  private final PreAdmissionScreeningRepository preRepo;
  private final ResidentRepository residentRepo;

  @Override
  @Transactional
  public PreResponse create(PreCreateRequest req) {
    ResidentEntity resident = residentRepo.findById(req.getResidentId())
        .orElseThrow(() -> new RuntimeException("Resident not found"));

    preRepo.findByResidentIdAndIsCurrentTrue(resident.getId())
        .ifPresent(old -> {
          if ("DRAFT".equals(old.getStatus())) {
            throw new RuntimeException("Resident already has a draft pre-admission screening in progress");
          }
          old.setIsCurrent(false);
          preRepo.save(old);
        });

    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setResident(resident);
    pre.setStatus("DRAFT");
    pre.setIsCurrent(true);
    UserEntity u = new UserEntity();
    // u.setId(SecurityUtils.getCurrentUserId());
    u.setId(1L);
    pre.setScreenedBy(u);
    pre.setCreatedAt(OffsetDateTime.now());
    return toResponse(preRepo.save(pre));
  }

  @Override
  public Page<PreResponse> listPaged(Pageable pageable) {
    return preRepo.findAll(pageable).map(this::toResponse);
  }

  @Override
  public List<PreSelectDTO> listCompletedForSelect() {
    return preRepo.findByStatusAndIsCurrentTrue("COMPLETED").stream().map(p -> {
      PreSelectDTO dto = new PreSelectDTO();
      dto.setId(p.getId());
      dto.setResidentName(p.getResident().getFirstName() + " " + p.getResident().getLastName());
      return dto;
    }).toList();
  }

  @Override
  @Transactional
  public PreResponse decide(Long id, PreDecisionRequest req) {
    PreAdmissionScreeningEntity pre = preRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));
    if (!req.getStatus().equals("COMPLETED") && !req.getStatus().equals("REJECTED"))
      throw new RuntimeException("Invalid status");
    pre.setStatus(req.getStatus());
    return toResponse(preRepo.save(pre));
  }

  @Override
  @Transactional
  public void deleteDraft(Long id) {
    PreAdmissionScreeningEntity pre = preRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Not found"));
    if (!"DRAFT".equals(pre.getStatus()))
      throw new RuntimeException("Only DRAFT can be deleted");
    preRepo.delete(pre);
  }

  private PreResponse toResponse(PreAdmissionScreeningEntity p) {
    PreResponse dto = new PreResponse();
    dto.setId(p.getId());
    dto.setStatus(p.getStatus());
    dto.setResidentId(p.getResident().getId());
    dto.setResidentName(p.getResident().getFirstName() + " " + p.getResident().getLastName());
    dto.setCreatedAt(p.getCreatedAt());
    return dto;
  }
}
