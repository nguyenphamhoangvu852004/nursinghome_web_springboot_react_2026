package com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository;

import com.eldercare.common.enums.BedStatus;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<BedEntity, Long> {
    boolean existsByRoomIdAndBedNumber(Long roomId, String bedNumber);
    boolean existsByRoomIdAndStatus(Long roomId, BedStatus status);
    List<BedEntity> findByRoomId(Long roomId);
    Optional<BedEntity> findByBedNumberAndRoomId(String bedNumber, Long roomId);

    @Query(value = """
            SELECT
                b.id            AS id,
                b.bed_number    AS bedNumber,
                b.status        AS status,
                b.room_id       AS roomId,
                CASE
                    WHEN r.id IS NULL THEN NULL
                    ELSE CONCAT(r.first_name, ' ', r.last_name)
                END             AS residentName,
                a.admission_date AS admissionDate
            FROM beds b
            LEFT JOIN residents r ON r.bed_id = b.id AND r.status = 'ACTIVE'
            LEFT JOIN (
                SELECT a1.resident_id, a1.admission_date
                FROM admissions a1
                INNER JOIN (
                    SELECT resident_id, MAX(id) AS max_id
                    FROM admissions
                    GROUP BY resident_id
                ) a2 ON a1.resident_id = a2.resident_id AND a1.id = a2.max_id
            ) a ON a.resident_id = r.id
            WHERE b.room_id IN (:roomIds)
            """, nativeQuery = true)
    List<BedProjection> findEnrichedBedsByRoomIds(@Param("roomIds") List<Long> roomIds);

    interface BedProjection {
        Long getId();
        String getBedNumber();
        String getStatus();
        Long getRoomId();
        String getResidentName();
        LocalDate getAdmissionDate();
    }
}
