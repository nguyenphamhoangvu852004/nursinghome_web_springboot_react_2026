package com.eldercare.modules.risk_incident.incident_tracking.service.impl;

import com.eldercare.modules.risk_incident.incident_tracking.dto.reponse.IncidentSeverityResponse;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.CreateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.dto.request.UpdateIncidentSeverityRequest;
import com.eldercare.modules.risk_incident.incident_tracking.entity.IncidentSeverityEntity;
import com.eldercare.modules.risk_incident.incident_tracking.repository.IncidentSeverityRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentSeverityServiceImplTest {

    @Mock
    private IncidentSeverityRepository incidentSeverityRepository;

    @InjectMocks
    private IncidentSeverityServiceImpl incidentSeverityService;

    private IncidentSeverityEntity severity;

    @BeforeEach
    void setUp() {
        severity = IncidentSeverityEntity.builder()
                .id(1L)
                .levelName("High")
                .chartLockTrigger(true)
                .description("High severity incident")
                .example("Resident fall")
                .build();
    }

    @Test
    @DisplayName("Get all severity levels - Success")
    void getAllSeverityLevels_Success() {

        when(incidentSeverityRepository.findAll())
                .thenReturn(List.of(severity));

        List<IncidentSeverityResponse> result =
                incidentSeverityService.getAllSeverityLevels();

        assertEquals(1, result.size());
        assertEquals("High", result.get(0).getLevelName());
        assertEquals(true, result.get(0).getChartLockTrigger());
        assertEquals("High severity incident", result.get(0).getDescription());
        assertEquals("Resident fall", result.get(0).getExample());

        verify(incidentSeverityRepository).findAll();
    }

    @Test
    @DisplayName("Create severity - Success")
    void createSeverityLevel_Success() {

        CreateIncidentSeverityRequest request =
                CreateIncidentSeverityRequest.builder()
                        .levelName("Critical")
                        .chartLockTrigger(true)
                        .description("Critical incident")
                        .example("Cardiac arrest")
                        .build();

        IncidentSeverityEntity savedEntity =
                IncidentSeverityEntity.builder()
                        .id(2L)
                        .levelName("Critical")
                        .chartLockTrigger(true)
                        .description("Critical incident")
                        .example("Cardiac arrest")
                        .build();

        when(incidentSeverityRepository.save(any()))
                .thenReturn(savedEntity);

        IncidentSeverityResponse response =
                incidentSeverityService.createSeverityLevel(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Critical", response.getLevelName());
        assertEquals(true, response.getChartLockTrigger());
        assertEquals("Critical incident", response.getDescription());
        assertEquals("Cardiac arrest", response.getExample());

        verify(incidentSeverityRepository).save(any());
    }

    @Test
    @DisplayName("Update severity - Success")
    void updateSeverityLevel_Success() {

        UpdateIncidentSeverityRequest request =
                UpdateIncidentSeverityRequest.builder()
                        .levelName("Medium")
                        .chartLockTrigger(false)
                        .description("Updated description")
                        .example("Updated example")
                        .build();

        when(incidentSeverityRepository.findById(1L))
                .thenReturn(Optional.of(severity));

        when(incidentSeverityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IncidentSeverityResponse response =
                incidentSeverityService.updateSeverityLevel(1L, request);

        assertEquals("Medium", response.getLevelName());
        assertFalse(response.getChartLockTrigger());
        assertEquals("Updated description", response.getDescription());
        assertEquals("Updated example", response.getExample());

        verify(incidentSeverityRepository).findById(1L);
        verify(incidentSeverityRepository).save(any());
    }

    @Test
    @DisplayName("Update severity - Not Found")
    void updateSeverityLevel_NotFound() {

        UpdateIncidentSeverityRequest request =
                UpdateIncidentSeverityRequest.builder()
                        .levelName("Low")
                        .chartLockTrigger(false)
                        .description("Desc")
                        .example("Example")
                        .build();

        when(incidentSeverityRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> incidentSeverityService.updateSeverityLevel(99L, request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(incidentSeverityRepository).findById(99L);
        verify(incidentSeverityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update severity - Only description and example")
    void updateSeverityLevel_UpdateDescriptionExampleOnly() {

        UpdateIncidentSeverityRequest request =
                UpdateIncidentSeverityRequest.builder()
                        .description("New Description")
                        .example("New Example")
                        .build();

        when(incidentSeverityRepository.findById(1L))
                .thenReturn(Optional.of(severity));

        when(incidentSeverityRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IncidentSeverityResponse response =
                incidentSeverityService.updateSeverityLevel(1L, request);

        assertEquals("High", response.getLevelName());
        assertTrue(response.getChartLockTrigger());
        assertEquals("New Description", response.getDescription());
        assertEquals("New Example", response.getExample());

        verify(incidentSeverityRepository).save(any());
    }

}