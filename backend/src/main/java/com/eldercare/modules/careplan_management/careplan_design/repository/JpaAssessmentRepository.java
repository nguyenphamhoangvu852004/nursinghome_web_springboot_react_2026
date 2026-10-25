package com.eldercare.modules.careplan_management.careplan_design.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eldercare.modules.resident_intake.assessment.AssessmentEntity;

public interface JpaAssessmentRepository extends JpaRepository<AssessmentEntity, Long> {
    @Query("""
                SELECT a.resident.id, a.confirmedCareLevel.id
                FROM AssessmentEntity a
                WHERE a.resident.id IN :residentIds
                  AND a.isCurrent = true
            """)
    List<Object[]> findCurrentResidentTier(@Param("residentIds") List<Long> residentIds);
}
