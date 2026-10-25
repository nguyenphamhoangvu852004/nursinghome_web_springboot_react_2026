package com.eldercare.modules.admin.facility_setup.facility.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityCreateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityUpdateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilityResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilitySelectResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.service.FacilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for FacilityController using Equivalence Partitioning technique.
 * Since we bypassed Spring Security for development as per comments in the controller,
 * we focus on input partitions directly handled by the controller endpoints.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityController - Equivalence Partitioning Tests")
class FacilityControllerTests {

    private MockMvc mockMvc;

    @Mock
    private FacilityService facilityService;

    @InjectMocks
    private FacilityController facilityController;

    private FacilityResponse facilityResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(facilityController).build();

        facilityResponse = new FacilityResponse();
        facilityResponse.setId(1L);
        facilityResponse.setFacilityCode("FAC-001");
        facilityResponse.setName("Test Facility");
    }

    @Nested
    @DisplayName("GET /facilities")
    class GetFacilitiesTests {
        
        /**
         * EP1: Default pagination parameters (page=0, size=10, search=null)
         * EP2: Custom pagination parameters with search string
         */
        
        @Test
        @DisplayName("EP1: Default parameters -> Returns 200 OK with page 0, size 10")
        void getFacilities_DefaultParams_ReturnsOk() throws Exception {
            PagedResponse<List<FacilityResponse>> response = PagedResponse.of(
                    List.of(facilityResponse), 200, "Success", 0, 1, 10, 1);
            
            when(facilityService.getFacilities(0, 10, null)).thenReturn(response);

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1L));
        }

        @Test
        @DisplayName("EP2: Custom parameters -> Returns 200 OK with custom page, size, and search")
        void getFacilities_CustomParams_ReturnsOk() throws Exception {
            PagedResponse<List<FacilityResponse>> response = PagedResponse.of(
                    List.of(facilityResponse), 200, "Success", 1, 5, 20, 100);
            
            when(facilityService.getFacilities(1, 20, "Test")).thenReturn(response);

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES)
                    .param("page", "1")
                    .param("size", "20")
                    .param("search", "Test")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /facilities")
    class CreateFacilityTests {
        
        @Test
        @DisplayName("EP1: Valid request body -> Returns 201 Created")
        void createFacility_ValidRequest_ReturnsCreated() throws Exception {
            when(facilityService.createFacility(any(FacilityCreateRequest.class))).thenReturn(facilityResponse);

            String requestBody = "{\"facilityCode\":\"FAC-001\",\"name\":\"Test Facility\"}";

            mockMvc.perform(post(RouteConstants.API_ADMIN_FACILITIES)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.facility_code").value("FAC-001"));
        }
    }

    @Nested
    @DisplayName("GET /facilities/{id}")
    class GetFacilityInfoTests {
        
        @Test
        @DisplayName("EP1: Valid ID -> Returns 200 OK with facility details")
        void getFacilityInfo_ValidId_ReturnsOk() throws Exception {
            when(facilityService.getFacilityInfo(1L)).thenReturn(facilityResponse);

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES + "/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));
        }
    }

    @Nested
    @DisplayName("PUT /facilities/{id}")
    class UpdateFacilityTests {
        
        @Test
        @DisplayName("EP1: Valid update request -> Returns 200 OK")
        void updateFacilityInfo_ValidRequest_ReturnsOk() throws Exception {
            when(facilityService.updateFacilityInfo(eq(1L), any(FacilityUpdateRequest.class)))
                    .thenReturn(facilityResponse);

            String requestBody = "{\"name\":\"Updated Facility\"}";

            mockMvc.perform(put(RouteConstants.API_ADMIN_FACILITIES + "/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));
        }
    }

    @Nested
    @DisplayName("GET /facilities/select")
    class GetFacilitiesForSelectTests {
        
        @Test
        @DisplayName("EP1: Valid request -> Returns 200 OK with select options")
        void getFacilitiesForSelect_ReturnsOk() throws Exception {
            FacilitySelectResponse selectResponse = new FacilitySelectResponse(1L, "Test Facility", java.util.Collections.emptyList());
            when(facilityService.getFacilitiesForSelect()).thenReturn(List.of(selectResponse));

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES + "/select")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1L))
                    .andExpect(jsonPath("$.data[0].name").value("Test Facility"));
        }
    }
}
