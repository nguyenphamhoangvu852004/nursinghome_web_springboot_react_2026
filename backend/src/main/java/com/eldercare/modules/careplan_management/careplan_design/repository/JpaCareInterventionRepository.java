package com.eldercare.modules.careplan_management.careplan_design.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;

public interface JpaCareInterventionRepository extends JpaRepository<CareInterventionEntity, Long> {

}
