package com.eldercare.modules.risk_incident.incident_tracking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentRequest;
import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentResponse;

public interface IncidentService {
    Page<IncidentResponse> getIncidents(Pageable pageable);

    IncidentResponse getIncidentById(Long id);

    IncidentResponse createIncident(CreateIncidentRequest request);
}