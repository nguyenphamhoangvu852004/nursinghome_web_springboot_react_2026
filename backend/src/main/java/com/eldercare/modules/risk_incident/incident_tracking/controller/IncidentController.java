package com.eldercare.modules.risk_incident.incident_tracking.controller;

import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentRequest;
import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentResponse;
import com.eldercare.modules.risk_incident.incident_tracking.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    @PreAuthorize("hasAnyRole('NURSE','CNA','DON','ADMIN')")
    @GetMapping
    public ResponseEntity<Page<IncidentResponse>> getIncidents(Pageable pageable) {
        Page<IncidentResponse> incidents = service.getIncidents(pageable);
        return ResponseEntity.ok(incidents);
    }

    @PreAuthorize("hasAnyRole('NURSE','CNA','DON','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable Long id) {
        IncidentResponse incident = service.getIncidentById(id);
        return ResponseEntity.ok(incident);
    }

    @PreAuthorize("hasAnyRole('NURSE','CNA','DON','ADMIN')")
    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody CreateIncidentRequest request) {
        IncidentResponse incident = service.createIncident(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(incident);
    }
}