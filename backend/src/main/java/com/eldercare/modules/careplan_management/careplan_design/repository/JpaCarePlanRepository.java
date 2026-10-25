package com.eldercare.modules.careplan_management.careplan_design.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;

public interface JpaCarePlanRepository
        extends JpaRepository<CarePlanEntity, Long>, JpaSpecificationExecutor<CarePlanEntity> {

}
