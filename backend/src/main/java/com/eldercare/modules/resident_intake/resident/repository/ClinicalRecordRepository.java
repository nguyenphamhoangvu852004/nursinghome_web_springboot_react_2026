package com.eldercare.modules.resident_intake.resident.repository;


import com.eldercare.modules.clinical.clinical_record.ClinicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecordEntity, Long> {
    List<ClinicalRecordEntity> findByResidentIdAndIsDeletedFalse(Long residentId);
    List<ClinicalRecordEntity> findByResidentIdAndRecordTypeAndIsDeletedFalse(Long residentId, String recordType);
}
