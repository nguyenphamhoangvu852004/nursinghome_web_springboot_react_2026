package com.eldercare.modules.admin.facility_setup.inventory.category.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategoryEntity, Long> {

    
    
}
