package com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<FacilityEntity, Long> {
    Optional<FacilityEntity> findByFacilityCode(String facilityCode);

    Page<FacilityEntity> findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
            String code, String name, String license, Pageable pageable);
    List<FacilityEntity> findByIsDeletedFalse();
}
