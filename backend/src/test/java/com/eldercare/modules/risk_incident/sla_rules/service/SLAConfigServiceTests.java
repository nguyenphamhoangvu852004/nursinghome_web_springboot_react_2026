package com.eldercare.modules.risk_incident.sla_rules.service.impl;

import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentSeverityRepository;
import com.eldercare.modules.risk_incident.sla_rules.dto.CreateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.dto.SLAResponse;
import com.eldercare.modules.risk_incident.sla_rules.dto.UpdateSLARequest;
import com.eldercare.modules.risk_incident.sla_rules.entity.SLAConfigEntity;
import com.eldercare.modules.risk_incident.sla_rules.repository.SLAConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SLAConfigServiceImplTest {

    @Mock
    private SLAConfigRepository slaConfigRepository;

    @Mock
    private IncidentSeverityRepository incidentSeverityRepository;

    @InjectMocks
    private SLAConfigServiceImpl slaConfigService;

    private IncidentSeverityEntity severity;

    private SLAConfigEntity slaConfig;

    @BeforeEach
    void setUp() {

        severity = IncidentSeverityEntity.builder()
                .id(1L)
                .levelName("High")
                .chartLockTrigger(true)
                .description("High severity")
                .example("Resident fall")
                .build();

        slaConfig = SLAConfigEntity.builder()
                .id(1L)
                .severity(severity)
                .slaWindowHrs(24)
                .externalReportRequired(true)
                .regulatoryBody("MOH")
                .build();
    }

    @Test
    @DisplayName("Get all SLA configs - Success")
    void getAllSLAConfigs_Success() {

        when(slaConfigRepository.findAll())
                .thenReturn(List.of(slaConfig));

        List<SLAResponse> result = slaConfigService.getAllSLAConfigs();

        assertEquals(1, result.size());
        assertEquals(24, result.get(0).getSlaWindowHrs());
        assertEquals(1L, result.get(0).getSeverityId());
        assertTrue(result.get(0).getExternalReportRequired());
        assertEquals("MOH", result.get(0).getRegulatoryBody());

        verify(slaConfigRepository).findAll();
    }

    @Test
    @DisplayName("Create SLA config - Success")
    void createSLAConfig_Success() {

        CreateSLARequest request = CreateSLARequest.builder()
                .severityId(1L)
                .slaWindowHrs(48)
                .externalReportRequired(false)
                .regulatoryBody("CDC")
                .build();

        SLAConfigEntity savedEntity = SLAConfigEntity.builder()
                .id(2L)
                .severity(severity)
                .slaWindowHrs(48)
                .externalReportRequired(false)
                .regulatoryBody("CDC")
                .build();

        when(incidentSeverityRepository.findById(1L))
                .thenReturn(Optional.of(severity));

        when(slaConfigRepository.save(any()))
                .thenReturn(savedEntity);

        SLAResponse response = slaConfigService.createSLAConfig(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(48, response.getSlaWindowHrs());
        assertFalse(response.getExternalReportRequired());
        assertEquals("CDC", response.getRegulatoryBody());

        verify(incidentSeverityRepository).findById(1L);
        verify(slaConfigRepository).save(any());
    }

    @Test
    @DisplayName("Create SLA config - Severity Not Found")
    void createSLAConfig_SeverityNotFound() {

        CreateSLARequest request = CreateSLARequest.builder()
                .severityId(99L)
                .slaWindowHrs(24)
                .externalReportRequired(true)
                .regulatoryBody("WHO")
                .build();

        when(incidentSeverityRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> slaConfigService.createSLAConfig(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(slaConfigRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update SLA config - Success")
    void updateSLAConfig_Success() {

        UpdateSLARequest request = UpdateSLARequest.builder()
                .severityId(1L)
                .slaWindowHrs(72)
                .externalReportRequired(false)
                .regulatoryBody("Department of Health")
                .build();

        when(slaConfigRepository.findById(1L))
                .thenReturn(Optional.of(slaConfig));

        when(incidentSeverityRepository.findById(1L))
                .thenReturn(Optional.of(severity));

        when(slaConfigRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SLAResponse response = slaConfigService.updateSLAConfig(1L, request);

        assertEquals(72, response.getSlaWindowHrs());
        assertFalse(response.getExternalReportRequired());
        assertEquals("Department of Health", response.getRegulatoryBody());

        verify(slaConfigRepository).findById(1L);
        verify(incidentSeverityRepository).findById(1L);
        verify(slaConfigRepository).save(any());
    }

    @Test
    @DisplayName("Update SLA config - SLA Not Found")
    void updateSLAConfig_NotFound() {

        UpdateSLARequest request = UpdateSLARequest.builder()
                .severityId(1L)
                .slaWindowHrs(72)
                .externalReportRequired(false)
                .regulatoryBody("CDC")
                .build();

        when(slaConfigRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> slaConfigService.updateSLAConfig(99L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(slaConfigRepository).findById(99L);
        verify(slaConfigRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update SLA config - Severity Not Found")
    void updateSLAConfig_SeverityNotFound() {

        UpdateSLARequest request = UpdateSLARequest.builder()
                .severityId(999L)
                .slaWindowHrs(48)
                .externalReportRequired(true)
                .regulatoryBody("WHO")
                .build();

        when(slaConfigRepository.findById(1L))
                .thenReturn(Optional.of(slaConfig));

        when(incidentSeverityRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> slaConfigService.updateSLAConfig(1L, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(slaConfigRepository).findById(1L);
        verify(incidentSeverityRepository).findById(999L);
        verify(slaConfigRepository, never()).save(any());
    }
}