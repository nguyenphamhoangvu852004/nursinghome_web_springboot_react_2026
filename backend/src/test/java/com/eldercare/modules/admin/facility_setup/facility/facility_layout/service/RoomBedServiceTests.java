package com.eldercare.modules.admin.facility_setup.facility.facility_layout.service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.enums.BedStatus;
import com.eldercare.common.enums.RoomType;
import com.eldercare.exception.custom.BadRequestException;
import com.eldercare.exception.custom.ResourceNotFoundException;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.BedRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.RoomRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.BedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.RoomResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.RoomEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.mapper.RoomBedMapper;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.BedRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.RoomRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.service.impl.RoomBedServiceImpl;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RoomBedServiceImpl using Equivalence Partitioning technique.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RoomBedService - Equivalence Partitioning Tests")
class RoomBedServiceTests {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BedRepository bedRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private RoomBedMapper mapper;

    @InjectMocks
    private RoomBedServiceImpl roomBedService;

    private FacilityEntity facility;
    private RoomEntity room;
    private BedEntity bed;
    private RoomResponse roomResponse;
    private BedResponse bedResponse;

    @BeforeEach
    void setUp() {
        facility = FacilityEntity.builder().id(1L).name("Test Facility").build();

        room = RoomEntity.builder()
                .id(1L)
                .roomNumber("101")
                .roomType(RoomType.PRIVATE)
                .facility(facility)
                .build();

        bed = BedEntity.builder()
                .id(1L)
                .bedNumber("101-A")
                .status(BedStatus.AVAILABLE)
                .room(room)
                .build();

        roomResponse = new RoomResponse();
        roomResponse.setId(1L);
        roomResponse.setRoomNumber("101");

        bedResponse = new BedResponse();
        bedResponse.setId(1L);
        bedResponse.setBedNumber("101-A");
    }

    // =========================================================================
    // ROOM OPERATIONS
    // =========================================================================

    @Nested
    @DisplayName("getRoomList - Equivalence Partitioning")
    class GetRoomListTests {
        @Test
        @DisplayName("EP1: search is null/blank -> gets all rooms for facility")
        void getRoomList_SearchBlank_ReturnsAllFacilityRooms() {
            int page = 0, size = 10;
            Page<RoomEntity> pageResult = new PageImpl<>(List.of(room));
            when(roomRepository.findByFacilityId(eq(1L), any(Pageable.class))).thenReturn(pageResult);
            when(bedRepository.findEnrichedBedsByRoomIds(anyList())).thenReturn(Collections.emptyList());
            when(mapper.toRoomResponseList(anyList(), anyList())).thenReturn(List.of(roomResponse));

            PagedResponse<List<RoomResponse>> result = roomBedService.getRoomList(1L, page, size, "   ");

            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            verify(roomRepository).findByFacilityId(eq(1L), any(Pageable.class));
        }

        @Test
        @DisplayName("EP2: search is valid -> returns filtered rooms for facility")
        void getRoomList_SearchValid_ReturnsFilteredRooms() {
            int page = 0, size = 10;
            String search = "101";
            Page<RoomEntity> pageResult = new PageImpl<>(List.of(room));
            when(roomRepository.findByFacilityIdAndRoomNumberContainingIgnoreCase(eq(1L), eq(search), any(Pageable.class))).thenReturn(pageResult);
            when(bedRepository.findEnrichedBedsByRoomIds(anyList())).thenReturn(Collections.emptyList());
            when(mapper.toRoomResponseList(anyList(), anyList())).thenReturn(List.of(roomResponse));

            PagedResponse<List<RoomResponse>> result = roomBedService.getRoomList(1L, page, size, search);

            assertThat(result).isNotNull();
            assertThat(result.getData()).hasSize(1);
            verify(roomRepository).findByFacilityIdAndRoomNumberContainingIgnoreCase(eq(1L), eq(search), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("createRoom - Equivalence Partitioning")
    class CreateRoomTests {
        @Test
        @DisplayName("EP1: roomNumber already exists in facility -> throws BadRequestException")
        void createRoom_DuplicateRoom_ThrowsBadRequestException() {
            RoomRequest request = new RoomRequest();
            request.setRoomNumber("101");

            when(roomRepository.existsByFacilityIdAndRoomNumber(1L, "101")).thenReturn(true);

            assertThatThrownBy(() -> roomBedService.createRoom(1L, request))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("RoomEntity number already exists");
        }

        @Test
        @DisplayName("EP2: facility not found -> throws ResourceNotFoundException")
        void createRoom_FacilityNotFound_ThrowsResourceNotFoundException() {
            RoomRequest request = new RoomRequest();
            request.setRoomNumber("102");

            when(roomRepository.existsByFacilityIdAndRoomNumber(1L, "102")).thenReturn(false);
            when(facilityRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> roomBedService.createRoom(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Facility not found");
        }

        @Test
        @DisplayName("EP3: valid data -> saves and returns room")
        void createRoom_ValidData_SavesAndReturnsRoom() {
            RoomRequest request = new RoomRequest();
            request.setRoomNumber("102");

            when(roomRepository.existsByFacilityIdAndRoomNumber(1L, "102")).thenReturn(false);
            when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
            when(mapper.toEntity(request)).thenReturn(room);
            when(roomRepository.save(room)).thenReturn(room);
            when(mapper.toResponse(eq(room), anyList())).thenReturn(roomResponse);

            RoomResponse result = roomBedService.createRoom(1L, request);

            assertThat(result).isNotNull();
            verify(roomRepository).save(room);
        }
    }

    @Nested
    @DisplayName("updateRoom - Equivalence Partitioning")
    class UpdateRoomTests {
        @Test
        @DisplayName("EP1: room not found -> throws ResourceNotFoundException")
        void updateRoom_RoomNotFound_ThrowsException() {
            RoomRequest request = new RoomRequest();
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> roomBedService.updateRoom(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("EP2: roomNumber changed and exists -> throws BadRequestException")
        void updateRoom_DuplicateRoomNumber_ThrowsException() {
            RoomRequest request = new RoomRequest();
            request.setRoomNumber("102"); // new number

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.existsByFacilityIdAndRoomNumber(1L, "102")).thenReturn(true);

            assertThatThrownBy(() -> roomBedService.updateRoom(1L, request))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        @DisplayName("EP3: valid update -> saves and returns")
        void updateRoom_ValidData_UpdatesAndReturns() {
            RoomRequest request = new RoomRequest();
            request.setRoomNumber("102");

            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(roomRepository.existsByFacilityIdAndRoomNumber(1L, "102")).thenReturn(false);
            when(roomRepository.save(any(RoomEntity.class))).thenReturn(room);
            when(bedRepository.findEnrichedBedsByRoomIds(anyList())).thenReturn(Collections.emptyList());
            when(mapper.toResponse(eq(room), anyList())).thenReturn(roomResponse);

            RoomResponse result = roomBedService.updateRoom(1L, request);

            assertThat(result).isNotNull();
            verify(roomRepository).save(room);
        }
    }

    @Nested
    @DisplayName("deleteRoom - Equivalence Partitioning")
    class DeleteRoomTests {
        @Test
        @DisplayName("EP1: room has occupied beds -> throws BadRequestException")
        void deleteRoom_OccupiedBeds_ThrowsException() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(bedRepository.existsByRoomIdAndStatus(1L, BedStatus.OCCUPIED)).thenReturn(true);

            assertThatThrownBy(() -> roomBedService.deleteRoom(1L))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("occupied beds");
        }

        @Test
        @DisplayName("EP2: valid to delete -> deletes room")
        void deleteRoom_Valid_DeletesRoom() {
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(bedRepository.existsByRoomIdAndStatus(1L, BedStatus.OCCUPIED)).thenReturn(false);

            roomBedService.deleteRoom(1L);

            verify(roomRepository).delete(room);
        }
    }

    // =========================================================================
    // BED OPERATIONS
    // =========================================================================

    @Nested
    @DisplayName("createBed - Equivalence Partitioning")
    class CreateBedTests {
        @Test
        @DisplayName("EP1: bedNumber exists in room -> throws BadRequestException")
        void createBed_DuplicateBed_ThrowsException() {
            BedRequest request = new BedRequest();
            request.setBedNumber("101-A");
            
            when(bedRepository.existsByRoomIdAndBedNumber(1L, "101-A")).thenReturn(true);
            
            assertThatThrownBy(() -> roomBedService.createBed(1L, request))
                    .isInstanceOf(BadRequestException.class);
        }
        
        @Test
        @DisplayName("EP2: valid data -> saves and returns bed")
        void createBed_ValidData_SavesBed() {
            BedRequest request = new BedRequest();
            request.setBedNumber("101-B");
            
            when(bedRepository.existsByRoomIdAndBedNumber(1L, "101-B")).thenReturn(false);
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
            when(mapper.toEntity(request)).thenReturn(bed);
            when(bedRepository.save(bed)).thenReturn(bed);
            when(mapper.toResponse(bed)).thenReturn(bedResponse);
            
            BedResponse result = roomBedService.createBed(1L, request);
            
            assertThat(result).isNotNull();
            verify(bedRepository).save(bed);
        }
    }

    @Nested
    @DisplayName("updateBedStatus - Equivalence Partitioning")
    class UpdateBedStatusTests {
        @Test
        @DisplayName("EP1: bedNumber changed and exists -> throws BadRequestException")
        void updateBed_DuplicateBedNumber_ThrowsException() {
            BedRequest request = new BedRequest();
            request.setBedNumber("101-B"); // Changed
            
            when(bedRepository.findById(1L)).thenReturn(Optional.of(bed));
            when(bedRepository.existsByRoomIdAndBedNumber(1L, "101-B")).thenReturn(true);
            
            assertThatThrownBy(() -> roomBedService.updateBedStatus(1L, request))
                    .isInstanceOf(BadRequestException.class);
        }
        
        @Test
        @DisplayName("EP2: valid update -> saves and returns")
        void updateBed_Valid_SavesBed() {
            BedRequest request = new BedRequest();
            request.setStatus(BedStatus.MAINTENANCE);
            
            when(bedRepository.findById(1L)).thenReturn(Optional.of(bed));
            when(bedRepository.save(bed)).thenReturn(bed);
            when(mapper.toResponse(bed)).thenReturn(bedResponse);
            
            BedResponse result = roomBedService.updateBedStatus(1L, request);
            
            assertThat(result).isNotNull();
            verify(bedRepository).save(bed);
        }
    }

    @Nested
    @DisplayName("deleteBed - Equivalence Partitioning")
    class DeleteBedTests {
        @Test
        @DisplayName("EP1: bed is occupied -> throws BadRequestException")
        void deleteBed_Occupied_ThrowsException() {
            bed.setStatus(BedStatus.OCCUPIED);
            when(bedRepository.findById(1L)).thenReturn(Optional.of(bed));
            
            assertThatThrownBy(() -> roomBedService.deleteBed(1L))
                    .isInstanceOf(BadRequestException.class);
        }
        
        @Test
        @DisplayName("EP2: bed is available -> deletes bed")
        void deleteBed_Available_DeletesBed() {
            bed.setStatus(BedStatus.AVAILABLE);
            when(bedRepository.findById(1L)).thenReturn(Optional.of(bed));
            
            roomBedService.deleteBed(1L);
            
            verify(bedRepository).delete(bed);
        }
    }
}
