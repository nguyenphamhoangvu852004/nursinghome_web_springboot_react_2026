package com.eldercare.modules.risk_incident.incident_tracking.controller;

import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentSeverityResponse;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.UpdateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.service.IncidentSeverityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incident-severity-levels")
@RequiredArgsConstructor
@Validated
public class IncidentSeverityController { 

    private final IncidentSeverityService incidentSeverityService;

    @GetMapping
    public List<IncidentSeverityResponse> getAllIncidentSeverityLevels() {
        return incidentSeverityService.getAllSeverityLevels();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentSeverityResponse createIncidentSeverityLevel(
            @Valid @RequestBody CreateIncidentSeverityRequest request) {
        return incidentSeverityService.createSeverityLevel(request);
    }

    @PutMapping("/{severityId}")
    public IncidentSeverityResponse updateIncidentSeverityLevel(
            @PathVariable Long severityId,
            @Valid @RequestBody UpdateIncidentSeverityRequest request) {
        return incidentSeverityService.updateSeverityLevel(severityId, request);
    }
}
                                                                                                                