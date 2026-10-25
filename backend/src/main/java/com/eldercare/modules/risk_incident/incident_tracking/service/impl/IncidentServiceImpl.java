package com.eldercare.modules.risk_incident.incident_tracking.service.impl;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;
import com.eldercare.modules.risk_incident.incident_timeline.entity.IncidentTimelineEntity;
import com.eldercare.modules.risk_incident.incident_timeline.repository.IncidentTimelineRepository;
import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentResponse;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentRequest;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentEntity;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentStatus;
import com.eldercare.modules.risk_incident.incident_tracking.mapper.IncidentMapper;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentRepository;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentSeverityRepository;
import com.eldercare.modules.risk_incident.incident_tracking.service.IncidentService;
import com.eldercare.modules.risk_incident.sla_rules.entity.SLAConfigEntity;
import com.eldercare.modules.risk_incident.sla_rules.repository.SLAConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentSeverityRepository severityRepository;
    private final SLAConfigRepository slaConfigRepository;
    private final ResidentRepository residentRepository;
    private final UserRepository userRepository;
    private final IncidentTimelineRepository timelineRepository;
    private final IncidentMapper mapper;


    @Override
    @Transactional
    public IncidentResponse createIncident(CreateIncidentRequest request) {

        ResidentEntity resident = residentRepository.findById(request.residentID())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Resident not found: " + request.residentID()
                        ));


        IncidentSeverityEntity severity = severityRepository.findById(request.severityID())
                .orElseThrow(() ->
                        new NotFoundException(
                                "Severity not found: " + request.severityID()
                        ));


        // TODO: Replace by authenticated user from JWT
        Long currentUserId = 1L;

        UserEntity reporter = userRepository.findById(currentUserId)
                .orElseThrow(() ->
                        new NotFoundException(
                                "Reporter not found: " + currentUserId
                        ));


        Integer slaHours = slaConfigRepository
                .findBySeverityId(severity.getId())
                .map(SLAConfigEntity::getSlaWindowHrs)
                .orElse(24);


        OffsetDateTime occurredTime = request.occurredAt()
                .atOffset(ZoneOffset.of("+07:00"));


        IncidentEntity incident = IncidentEntity.builder()
                .resident(resident)
                .severity(severity)
                .reporter(reporter)
                .incidentType(request.incidentType())
                .occurredAt(occurredTime)
                .slaDeadline(occurredTime.plusHours(slaHours))
                .location(request.location())
                .description(request.description())
                .witnesses(request.witnesses())
                .status(IncidentStatus.OPEN)
                .build();


        IncidentEntity savedIncident = incidentRepository.save(incident);


        // Lock medical chart if severity requires
        if (Boolean.TRUE.equals(severity.getChartLockTrigger())) {
            resident.setChartLocked(true);
            residentRepository.save(resident);
        }


        IncidentTimelineEntity timeline = IncidentTimelineEntity.builder()
                .incidentId(savedIncident.getId())
                .action("Incident reported")
                .actor(reporter)
                .createdAt(OffsetDateTime.now())
                .build();


        timelineRepository.save(timeline);


        return mapper.toDetail(
                savedIncident,
                slaHours,
                List.of(timeline)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Page<IncidentResponse> getIncidents(Pageable pageable) {

        return incidentRepository.findAll(pageable)
                .map(mapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public IncidentResponse getIncidentById(Long id) {

        IncidentEntity incident = incidentRepository.findWithDetailById(id);

        if (incident == null) {
            throw new NotFoundException(
                    "Incident not found: " + id
            );
        }


        Integer slaHours = slaConfigRepository
                .findBySeverityId(incident.getSeverity().getId())
                .map(SLAConfigEntity::getSlaWindowHrs)
                .orElse(null);


        List<IncidentTimelineEntity> timelines =
                timelineRepository.findByIncidentIdOrderByCreatedAtAsc(id);


        return mapper.toDetail(
                incident,
                slaHours,
                timelines
        );
    }
}