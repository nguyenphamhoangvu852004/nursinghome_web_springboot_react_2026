package com.eldercare.modules.risk_incident.incident_timeline.repository;


import com.eldercare.modules.risk_incident.incident_timeline.entity.IncidentTimelineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentTimelineRepository extends JpaRepository<IncidentTimelineEntity, Long> {

    List<IncidentTimelineEntity> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);

}