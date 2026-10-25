package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.entity.DurableMedicalEquipmentEntity;

public interface DurableMedicalEquipmentRepository extends JpaRepository<DurableMedicalEquipmentEntity, Long> {

    Optional<DurableMedicalEquipmentEntity> findByAssetTagAndIsDeletedFalse(String assetTag);

    Optional<DurableMedicalEquipmentEntity> findByIdAndStatusAndIsDeletedFalse(Long id, String status);
}
