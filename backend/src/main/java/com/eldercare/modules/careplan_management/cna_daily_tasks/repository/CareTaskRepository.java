package com.eldercare.modules.careplan_management.cna_daily_tasks.repository;

import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CareTaskRepository extends JpaRepository<CareTaskEntity, Long> {

    @Query("""
        SELECT new com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow(
            ct.id, ci.id, cp.id, r.id, CONCAT(r.firstName, ' ', r.lastName), rm.roomNumber, 
            u.id, CONCAT(u.firstName, ' ', u.lastName), 
            ct.scheduledTime, ct.completedAt, ct.taskType, CAST(ct.status AS string), ct.isAbnormalFlagged, ct.goal)
        FROM CareTaskEntity ct
        JOIN ct.careIntervention ci
        JOIN ci.careGoal cg
        JOIN cg.carePlanVersion cpv
        JOIN cpv.carePlan cp
        JOIN ResidentEntity r ON cp.residentId = r.id
        LEFT JOIN r.bed b
        LEFT JOIN b.room rm
        LEFT JOIN ct.assignedCna u
        WHERE (CAST(:startOfDay AS java.time.OffsetDateTime) IS NULL OR ct.scheduledTime >= :startOfDay)
          AND (CAST(:endOfDay AS java.time.OffsetDateTime) IS NULL OR ct.scheduledTime < :endOfDay)
          AND (:status IS NULL OR CAST(ct.status AS string) = :status)
          AND (:taskType IS NULL OR ct.taskType = :taskType)
          AND (:residentId IS NULL OR r.id = :residentId)
          AND (:assignedCnaId IS NULL OR u.id = :assignedCnaId)
          AND (:isAbnormalFlagged IS NULL OR ct.isAbnormalFlagged = :isAbnormalFlagged)
    """)
    List<EnrichedTaskRow> findEnrichedTasksForGrouping(
            @Param("startOfDay") OffsetDateTime startOfDay,
            @Param("endOfDay") OffsetDateTime endOfDay,
            @Param("status") String status,
            @Param("taskType") String taskType,
            @Param("residentId") Long residentId,
            @Param("assignedCnaId") Long assignedCnaId,
            @Param("isAbnormalFlagged") Boolean isAbnormalFlagged
    );

    @Query(value = """
        SELECT new com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow(
            ct.id, ci.id, cp.id, r.id, CONCAT(r.firstName, ' ', r.lastName), rm.roomNumber, 
            u.id, CONCAT(u.firstName, ' ', u.lastName), 
            ct.scheduledTime, ct.completedAt, ct.taskType, CAST(ct.status AS string), ct.isAbnormalFlagged, ct.goal)
        FROM CareTaskEntity ct
        JOIN ct.careIntervention ci
        JOIN ci.careGoal cg
        JOIN cg.carePlanVersion cpv
        JOIN cpv.carePlan cp
        JOIN com.eldercare.modules.resident_intake.resident_profile.ResidentEntity r ON cp.residentId = r.id
        LEFT JOIN r.bed b
        LEFT JOIN b.room rm
        LEFT JOIN ct.assignedCna u
        WHERE (CAST(:startOfDay AS java.time.OffsetDateTime) IS NULL OR ct.scheduledTime >= :startOfDay)
          AND (CAST(:endOfDay AS java.time.OffsetDateTime) IS NULL OR ct.scheduledTime < :endOfDay)
          AND (:status IS NULL OR CAST(ct.status AS string) = :status)
          AND (:taskType IS NULL OR ct.taskType = :taskType)
          AND (:residentId IS NULL OR r.id = :residentId)
          AND (:assignedCnaId IS NULL OR u.id = :assignedCnaId)
          AND (:isAbnormalFlagged IS NULL OR ct.isAbnormalFlagged = :isAbnormalFlagged)
    """,
    countQuery = """
        SELECT count(ct.id)
        FROM CareTaskEntity ct
        JOIN ct.careIntervention ci
        JOIN ci.careGoal cg
        JOIN cg.carePlanVersion cpv
        JOIN cpv.carePlan cp
        JOIN com.eldercare.modules.resident_intake.resident_profile.ResidentEntity r ON cp.residentId = r.id
        LEFT JOIN ct.assignedCna u
        WHERE (CAST(:startOfDay AS java.time.OffsetDateTime) IS NULL OR ct.scheduledTime >= :startOfDay)
          AND (CAST(:endOfDay AS java.time.OffsetDateTime) IS NULL OR ct.scheduledTime < :endOfDay)
          AND (:status IS NULL OR CAST(ct.status AS string) = :status)
          AND (:taskType IS NULL OR ct.taskType = :taskType)
          AND (:residentId IS NULL OR r.id = :residentId)
          AND (:assignedCnaId IS NULL OR u.id = :assignedCnaId)
          AND (:isAbnormalFlagged IS NULL OR ct.isAbnormalFlagged = :isAbnormalFlagged)
    """)
    Page<EnrichedTaskRow> searchEnrichedTasks(
            @Param("startOfDay") OffsetDateTime startOfDay,
            @Param("endOfDay") OffsetDateTime endOfDay,
            @Param("status") String status,
            @Param("taskType") String taskType,
            @Param("residentId") Long residentId,
            @Param("assignedCnaId") Long assignedCnaId,
            @Param("isAbnormalFlagged") Boolean isAbnormalFlagged,
            Pageable pageable
    );
    
    Page<CareTaskEntity> findByCareIntervention_Id(Long interventionId, Pageable pageable);
}
