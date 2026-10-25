package com.eldercare.modules.resident_intake.resident.repository;


import com.eldercare.modules.finance_billing.insurance_coverage.InsuranceProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InsuranceProviderRepository extends JpaRepository<InsuranceProviderEntity, Long> {
    Optional<InsuranceProviderEntity> findByProviderName(String providerName);
}
