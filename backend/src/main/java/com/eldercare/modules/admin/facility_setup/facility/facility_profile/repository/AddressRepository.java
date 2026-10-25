package com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
}
