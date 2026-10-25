package com.eldercare.modules.resident_intake.admission_ledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;

public interface AdmissionRepository extends JpaRepository<AdmissionEntity, Long> {
  Page<AdmissionEntity> findAll(Pageable pageable);

  Optional<AdmissionEntity> findByResidentIdAndIsCurrentTrue(Long residentId);

  List<AdmissionEntity> findByResidentId(Long residentId);

  List<AdmissionEntity> findByIsCurrentTrueAndDischargeDateIsNull();
}