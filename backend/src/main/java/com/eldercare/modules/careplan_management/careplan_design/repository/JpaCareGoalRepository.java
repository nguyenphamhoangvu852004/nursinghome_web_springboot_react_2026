package com.eldercare.modules.careplan_management.careplan_design.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eldercare.modules.careplan_management.careplan_design.entity.CareGoalEntity;

public interface JpaCareGoalRepository extends JpaRepository<CareGoalEntity, Long> {
    List<CareGoalEntity> findByCarePlanIdIn(List<Long> carePlanIds);
}
