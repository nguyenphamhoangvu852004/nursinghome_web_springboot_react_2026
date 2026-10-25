package com.eldercare.modules.risk_incident.incident_tracking.service.impl;

import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentSeverityResponse;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.UpdateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;
import com.eldercare.modules.risk_incident.incident_tracking.mapper.IncidentSeverityMapper;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentSeverityRepository;
import com.eldercare.modules.risk_incident.incident_tracking.service.IncidentSeverityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncidentSeverityServiceImpl implements IncidentSeverityService {

    private final IncidentSeverityRepository incidentSeverityRepository;

    @Override
    public List<IncidentSeverityResponse> getAllSeverityLevels() {
        return incidentSeverityRepository.findAll()
                .stream()
                .map(IncidentSeverityMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public IncidentSeverityResponse createSeverityLevel(CreateIncidentSeverityRequest request) {
        IncidentSeverityEntity saved = incidentSeverityRepository.save(
                IncidentSeverityMapper.toEntity(request)
        );

        return IncidentSeverityMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public IncidentSeverityResponse updateSeverityLevel(
            Long severityId,
            UpdateIncidentSeverityRequest request
    ) {
        IncidentSeverityEntity existing = incidentSeverityRepository.findById(severityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident severity level not found"
                ));

        if (request.getLevelName() != null) {
            existing.setLevelName(request.getLevelName());
        }

        if (request.getChartLockTrigger() != null) {
            existing.setChartLockTrigger(request.getChartLockTrigger());
        }

        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }

        if (request.getExample() != null) {
            existing.setExample(request.getExample());
        }

        return IncidentSeverityMapper.toResponse(
                incidentSeverityRepository.save(existing)
        );
    }
}