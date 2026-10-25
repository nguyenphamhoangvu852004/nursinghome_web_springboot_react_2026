package com.eldercare.modules.resident_intake.resident.repository;


import com.eldercare.modules.resident_intake.resident_profile.ResidentSensitiveInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResidentSensitiveInfoRepository extends JpaRepository<ResidentSensitiveInfoEntity, Long> {
    Optional<ResidentSensitiveInfoEntity> findByResidentId(Long residentId);
}
