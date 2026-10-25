package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.entity.ConsumableSupplyEntity;

public interface ConsumableSupplyRepository extends JpaRepository<ConsumableSupplyEntity, Long> {
    

    Page<ConsumableSupplyEntity> findByStatusAndIsDeletedFalse(String status, Pageable pageable);
}
