package com.eldercare.modules.admin.facility_setup.facility.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.enums.BedStatus;
import com.eldercare.common.enums.RoomType;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.BedRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.RoomRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.BedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.RoomResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.service.RoomBedService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for RoomBedController using Equivalence Partitioning technique.
 * Focuses on correct parameter mapping and endpoint routing.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoomBedController - Equivalence Partitioning Tests")
class RoomBedControllerTests {

    private MockMvc mockMvc;

    @Mock
    private RoomBedService roomBedService;

    @InjectMocks
    private RoomBedController roomBedController;

    private RoomResponse roomResponse;
    private BedResponse bedResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomBedController).build();

        roomResponse = new RoomResponse();
        roomResponse.setId(1L);
        roomResponse.setRoomNumber("101");
        roomResponse.setRoomType(RoomType.PRIVATE);

        bedResponse = new BedResponse();
        bedResponse.setId(1L);
        bedResponse.setBedNumber("101-A");
        bedResponse.setStatus(BedStatus.AVAILABLE);
    }

    // =========================================================================
    // ROOM ENDPOINTS
    // =========================================================================

    @Nested
    @DisplayName("GET /facilities/rooms")
    class GetAllRoomsTests {
        @Test
        @DisplayName("EP1: Default params -> calls getAllRooms with default page/size")
        void getAllRooms_ReturnsOk() throws Exception {
            PagedResponse<List<RoomResponse>> response = PagedResponse.of(
                    List.of(roomResponse), 200, "Success", 0, 1, 10, 1);
            
            when(roomBedService.getAllRooms(0, 10, null)).thenReturn(response);

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES + "/rooms")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /facilities/{facilityId}/rooms")
    class GetRoomListTests {
        @Test
        @DisplayName("EP1: Valid facility ID -> calls getRoomList")
        void getRoomList_ReturnsOk() throws Exception {
            PagedResponse<List<RoomResponse>> response = PagedResponse.of(
                    List.of(roomResponse), 200, "Success", 0, 1, 10, 1);
            
            when(roomBedService.getRoomList(1L, 0, 10, null)).thenReturn(response);

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /facilities/{facilityId}/rooms")
    class CreateRoomTests {
        @Test
        @DisplayName("EP1: Valid request -> Returns 201 Created")
        void createRoom_ReturnsCreated() throws Exception {
            when(roomBedService.createRoom(eq(1L), any(RoomRequest.class))).thenReturn(roomResponse);

            String requestBody = "{\"roomNumber\":\"101\"}";

            mockMvc.perform(post(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }
    }

    @Nested
    @DisplayName("PUT /facilities/{facilityId}/rooms/{roomId}")
    class UpdateRoomTests {
        @Test
        @DisplayName("EP1: Valid update -> Returns 200 OK")
        void updateRoom_ReturnsOk() throws Exception {
            when(roomBedService.updateRoom(eq(1L), any(RoomRequest.class))).thenReturn(roomResponse);

            String requestBody = "{\"roomNumber\":\"102\"}";

            mockMvc.perform(put(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /facilities/{facilityId}/rooms/{roomId}")
    class DeleteRoomTests {
        @Test
        @DisplayName("EP1: Valid delete -> Returns 204 No Content")
        void deleteRoom_ReturnsNoContent() throws Exception {
            mockMvc.perform(delete(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
                    
            verify(roomBedService).deleteRoom(1L);
        }
    }

    // =========================================================================
    // BED ENDPOINTS
    // =========================================================================

    @Nested
    @DisplayName("GET /facilities/{facilityId}/rooms/{roomId}/beds")
    class GetBedListTests {
        @Test
        @DisplayName("EP1: Valid IDs -> Returns list of beds")
        void getBedList_ReturnsOk() throws Exception {
            when(roomBedService.getBedListByRoomId(1L)).thenReturn(List.of(bedResponse));

            mockMvc.perform(get(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms/1/beds")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));
        }
    }

    @Nested
    @DisplayName("POST /facilities/{facilityId}/rooms/{roomId}/beds")
    class CreateBedTests {
        @Test
        @DisplayName("EP1: Valid request -> Returns 201 Created")
        void createBed_ReturnsCreated() throws Exception {
            when(roomBedService.createBed(eq(1L), any(BedRequest.class))).thenReturn(bedResponse);

            String requestBody = "{\"bedNumber\":\"101-A\"}";

            mockMvc.perform(post(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms/1/beds")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L));
        }
    }

    @Nested
    @DisplayName("PUT /facilities/{facilityId}/rooms/{roomId}/beds/{bedId}")
    class UpdateBedStatusTests {
        @Test
        @DisplayName("EP1: Valid update -> Returns 200 OK")
        void updateBed_ReturnsOk() throws Exception {
            when(roomBedService.updateBedStatus(eq(1L), any(BedRequest.class))).thenReturn(bedResponse);

            String requestBody = "{\"status\":\"MAINTENANCE\"}";

            mockMvc.perform(put(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms/1/beds/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /facilities/{facilityId}/rooms/{roomId}/beds/{bedId}")
    class DeleteBedTests {
        @Test
        @DisplayName("EP1: Valid delete -> Returns 204 No Content")
        void deleteBed_ReturnsNoContent() throws Exception {
            mockMvc.perform(delete(RouteConstants.API_ADMIN_FACILITIES + "/1/rooms/1/beds/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
                    
            verify(roomBedService).deleteBed(1L);
        }
    }
}
