package com.eldercare.modules.resident_intake.pre_admission.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity;

public interface PreAdmissionScreeningRepository extends JpaRepository<PreAdmissionScreeningEntity, Long> {
  Page<PreAdmissionScreeningEntity> findAll(Pageable pageable);

  List<PreAdmissionScreeningEntity> findByStatusAndIsCurrentTrue(String status);

  Optional<PreAdmissionScreeningEntity> findByResidentIdAndIsCurrentTrue(Long residentId);
}
