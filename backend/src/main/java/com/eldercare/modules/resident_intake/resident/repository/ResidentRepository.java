package com.eldercare.modules.resident_intake.resident.repository;

import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRepository
              extends JpaRepository<ResidentEntity, Long>,
              JpaSpecificationExecutor<ResidentEntity> {

       List<ResidentEntity> findByIsDeletedFalse();

       List<ResidentEntity> findByStatus(String status);

       @Query("""
                     SELECT r
                     FROM ResidentEntity r
                     WHERE r.isDeleted = false
                       AND (
                           :status IS NULL
                           OR UPPER(r.status) = UPPER(:status)
                       )
                       AND (
                           :bedId IS NULL
                           OR (
                               r.bed IS NOT NULL
                               AND r.bed.id = :bedId
                           )
                       )
                       AND (
                           :search IS NULL
                           OR LOWER(r.firstName)
                                 LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(r.lastName)
                                 LIKE LOWER(CONCAT('%', :search, '%'))
                           OR CAST(r.id AS string)
                                 LIKE CONCAT('%', :search, '%')
                       )
                     """)
       Page<ResidentEntity> findResidentsWithFilters(
                     @Param("status") String status,
                     @Param("bedId") Long bedId,
                     @Param("search") String search,
                     Pageable pageable);

       Optional<ResidentEntity> findByIdAndIsDeletedFalse(
                     Long id);

       boolean existsByIdAndIsDeletedFalse(
                     Long id);
}