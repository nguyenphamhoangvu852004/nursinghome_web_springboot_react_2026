package com.eldercare.modules.resident_intake.resident.repository;


import com.eldercare.modules.finance_billing.insurance_coverage.ResidentInsurancePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResidentInsurancePolicyRepository extends JpaRepository<ResidentInsurancePolicyEntity, Long> {
    List<ResidentInsurancePolicyEntity> findByResidentIdAndIsDeletedFalse(Long residentId);
}
