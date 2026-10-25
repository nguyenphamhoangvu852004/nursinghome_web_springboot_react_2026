package com.eldercare.modules.resident_intake.assessment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.resident_intake.assessment.AssessmentEntity;

public interface AssessmentRepository extends JpaRepository<AssessmentEntity, Long> {
  Page<AssessmentEntity> findAll(Pageable pageable);

  List<AssessmentEntity> findByStatusAndIsCurrentTrue(String status);

  Optional<AssessmentEntity> findByResidentIdAndIsCurrentTrue(Long residentId);
}
