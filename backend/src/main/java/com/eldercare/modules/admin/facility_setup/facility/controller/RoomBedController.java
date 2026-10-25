package com.eldercare.modules.admin.facility_setup.facility.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.BedRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.RoomRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.BedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.RoomResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.service.RoomBedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RouteConstants.API_ADMIN_FACILITIES)
@RequiredArgsConstructor
public class RoomBedController {

    private final RoomBedService roomBedService;

    // RoomEntity Endpoints

    @GetMapping("/rooms")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<PagedResponse<List<RoomResponse>>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(roomBedService.getAllRooms(page, size, search));
    }

    @GetMapping("/{facilityId}/rooms")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<PagedResponse<List<RoomResponse>>> getRoomList(
            @PathVariable Long facilityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(roomBedService.getRoomList(facilityId, page, size, search));
    }

    @PostMapping("/{facilityId}/rooms")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<RoomResponse> createRoom(@PathVariable Long facilityId, @RequestBody RoomRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomBedService.createRoom(facilityId, request));
    }

    @PutMapping("/{facilityId}/rooms/{roomId}")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long facilityId, @PathVariable Long roomId, @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomBedService.updateRoom(roomId, request));
    }

    @DeleteMapping("/{facilityId}/rooms/{roomId}")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<Void> deleteRoom(@PathVariable Long facilityId, @PathVariable Long roomId) {
        roomBedService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    // BedEntity Endpoints

    @GetMapping("/{facilityId}/rooms/{roomId}/beds")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<List<BedResponse>> getBedListByRoomId(@PathVariable Long facilityId, @PathVariable Long roomId) {
        return ResponseEntity.ok(roomBedService.getBedListByRoomId(roomId));
    }

    @PostMapping("/{facilityId}/rooms/{roomId}/beds")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<BedResponse> createBed(@PathVariable Long facilityId, @PathVariable Long roomId, @RequestBody BedRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomBedService.createBed(roomId, request));
    }

    @PutMapping("/{facilityId}/rooms/{roomId}/beds/{bedId}")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<BedResponse> updateBedStatus(@PathVariable Long facilityId, @PathVariable Long roomId, @PathVariable Long bedId, @RequestBody BedRequest request) {
        return ResponseEntity.ok(roomBedService.updateBedStatus(bedId, request));
    }

    @DeleteMapping("/{facilityId}/rooms/{roomId}/beds/{bedId}")
    // @PreAuthorize("hasRole('NHA_ADMIN')") // Bypassed for development
    public ResponseEntity<Void> deleteBed(@PathVariable Long facilityId, @PathVariable Long roomId, @PathVariable Long bedId) {
        roomBedService.deleteBed(bedId);
        return ResponseEntity.noContent().build();
    }
}
