package com.eldercare.modules.risk_incident.incident_tracking.repository;

import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<IncidentEntity, Long> {

    @EntityGraph(attributePaths = {"resident", "resident.bed", "resident.bed.room", "severity"})
    Page<IncidentEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"resident", "resident.bed", "resident.bed.room", "severity", "reporter"})
    IncidentEntity findWithDetailById(Long id);
}