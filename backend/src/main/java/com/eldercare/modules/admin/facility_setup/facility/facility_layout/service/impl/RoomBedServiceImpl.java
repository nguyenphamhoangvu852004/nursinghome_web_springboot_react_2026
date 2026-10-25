package com.eldercare.modules.admin.facility_setup.facility.facility_layout.service.impl;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.enums.BedStatus;
import com.eldercare.exception.custom.BadRequestException;
import com.eldercare.exception.custom.ResourceNotFoundException;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.BedRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.RoomRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.BedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.RoomResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.RoomEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.mapper.RoomBedMapper;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.BedRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.RoomRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.service.RoomBedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomBedServiceImpl implements RoomBedService {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final FacilityRepository facilityRepository;
    private final RoomBedMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<List<RoomResponse>> getRoomList(Long facilityId, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomEntity> roomPage;
        
        if (search != null && !search.trim().isEmpty()) {
            roomPage = roomRepository.findByFacilityIdAndRoomNumberContainingIgnoreCase(facilityId, search, pageable);
        } else {
            roomPage = roomRepository.findByFacilityId(facilityId, pageable);
        }
        
        List<RoomEntity> rooms = roomPage.getContent();
        List<Long> roomIds = rooms.stream().map(RoomEntity::getId).collect(Collectors.toList());
        
        List<BedRepository.BedProjection> enrichedBeds = List.of();
        if (!roomIds.isEmpty()) {
            enrichedBeds = bedRepository.findEnrichedBedsByRoomIds(roomIds);
        }

        List<RoomResponse> content = mapper.toRoomResponseList(rooms, enrichedBeds);
        
        return PagedResponse.of(content, 200, "Rooms retrieved successfully",
                page, roomPage.getTotalPages(), size, roomPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<List<RoomResponse>> getAllRooms(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomEntity> roomPage;
        
        if (search != null && !search.trim().isEmpty()) {
            roomPage = roomRepository.findByRoomNumberContainingIgnoreCase(search, pageable);
        } else {
            roomPage = roomRepository.findAllRooms(pageable);
        }
        
        List<RoomEntity> rooms = roomPage.getContent();
        List<Long> roomIds = rooms.stream().map(RoomEntity::getId).collect(Collectors.toList());
        
        List<BedRepository.BedProjection> enrichedBeds = List.of();
        if (!roomIds.isEmpty()) {
            enrichedBeds = bedRepository.findEnrichedBedsByRoomIds(roomIds);
        }

        List<RoomResponse> content = mapper.toRoomResponseList(rooms, enrichedBeds);
        
        return PagedResponse.of(content, 200, "All rooms retrieved successfully",
                page, roomPage.getTotalPages(), size, roomPage.getTotalElements());
    }

    @Override
    @Transactional
    public RoomResponse createRoom(Long facilityId, RoomRequest request) {
        if (roomRepository.existsByFacilityIdAndRoomNumber(facilityId, request.getRoomNumber())) {
            throw new BadRequestException("RoomEntity number already exists in this facility");
        }

        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        RoomEntity room = mapper.toEntity(request);
        room.setFacility(facility);
        room = roomRepository.save(room);
        
        // Return without beds initially
        return mapper.toResponse(room, List.of());
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Long roomId, RoomRequest request) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomEntity not found"));

        if (!room.getRoomNumber().equals(request.getRoomNumber()) &&
                roomRepository.existsByFacilityIdAndRoomNumber(room.getFacility().getId(), request.getRoomNumber())) {
            throw new BadRequestException("RoomEntity number already exists in this facility");
        }

        room.setRoomNumber(request.getRoomNumber());
        if (request.getRoomType() != null) {
            room.setRoomType(request.getRoomType());
        }
        
        room = roomRepository.save(room);
        
        List<BedRepository.BedProjection> enrichedBeds = bedRepository.findEnrichedBedsByRoomIds(List.of(roomId));
        return mapper.toResponse(room, enrichedBeds);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomEntity not found"));

        if (bedRepository.existsByRoomIdAndStatus(roomId, BedStatus.OCCUPIED)) {
            throw new BadRequestException("Cannot delete room. It contains occupied beds.");
        }

        roomRepository.delete(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BedResponse> getBedListByRoomId(Long roomId) {
        List<BedEntity> beds = bedRepository.findByRoomId(roomId);
        return mapper.toBedResponseList(beds);
    }

    @Override
    @Transactional
    public BedResponse createBed(Long roomId, BedRequest request) {
        if (bedRepository.existsByRoomIdAndBedNumber(roomId, request.getBedNumber())) {
            throw new BadRequestException("BedEntity number already exists in this room");
        }

        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomEntity not found"));

        BedEntity bed = mapper.toEntity(request);
        bed.setRoom(room);
        bed = bedRepository.save(bed);
        return mapper.toResponse(bed);
    }

    @Override
    @Transactional
    public BedResponse updateBedStatus(Long bedId, BedRequest request) {
        BedEntity bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("BedEntity not found"));

        if (request.getStatus() != null) {
            bed.setStatus(request.getStatus());
        }
        if (request.getBedNumber() != null && !request.getBedNumber().equals(bed.getBedNumber())) {
            if (bedRepository.existsByRoomIdAndBedNumber(bed.getRoom().getId(), request.getBedNumber())) {
                throw new BadRequestException("BedEntity number already exists in this room");
            }
            bed.setBedNumber(request.getBedNumber());
        }

        bed = bedRepository.save(bed);
        return mapper.toResponse(bed);
    }

    @Override
    @Transactional
    public void deleteBed(Long bedId) {
        BedEntity bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("BedEntity not found"));

        if (bed.getStatus() == BedStatus.OCCUPIED) {
            throw new BadRequestException("Cannot delete an occupied bed.");
        }

        bedRepository.delete(bed);
    }
}
