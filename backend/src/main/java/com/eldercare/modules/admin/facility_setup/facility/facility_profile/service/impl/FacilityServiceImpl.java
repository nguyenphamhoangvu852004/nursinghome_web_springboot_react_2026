package com.eldercare.modules.admin.facility_setup.facility.facility_profile.service.impl;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.exception.custom.BadRequestException;
import com.eldercare.exception.custom.ResourceNotFoundException;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityCreateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.FacilityUpdateRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilityResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.FacilitySelectResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.dto.response.BedSelectResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.dto.response.RoomSelectResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.RoomEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.BedRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.RoomRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.mapper.FacilityMapper;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<List<FacilityResponse>> getFacilities(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FacilityEntity> facilityPage;

        if (search != null && !search.trim().isEmpty()) {
            facilityPage = facilityRepository
                    .findByFacilityCodeContainingIgnoreCaseOrNameContainingIgnoreCaseOrLicenseNumberContainingIgnoreCase(
                            search, search, search, pageable);
        } else {
            facilityPage = facilityRepository.findAll(pageable);
        }

        List<FacilityResponse> content = facilityPage.getContent()
                .stream()
                .map(facilityMapper::toResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, 200, "Facilities retrieved successfully",
                page, facilityPage.getTotalPages(), size, facilityPage.getTotalElements());
    }

    @Override
    @Transactional
    public FacilityResponse createFacility(FacilityCreateRequest request) {
        if (facilityRepository.findByFacilityCode(request.getFacilityCode()).isPresent()) {
            throw new BadRequestException("Facility code already exists");
        }

        FacilityEntity facility = facilityMapper.toEntity(request);
        facility = facilityRepository.save(facility);
        return facilityMapper.toResponse(facility);
    }

    @Override
    @Transactional(readOnly = true)
    public FacilityResponse getFacilityInfo(Long facilityId) {
        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));
        return facilityMapper.toResponse(facility);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacilitySelectResponse> getFacilitiesForSelect() {

        List<FacilityEntity> facilities = facilityRepository.findByIsDeletedFalse();
        List<RoomEntity> rooms = roomRepository.findAll();
        List<BedEntity> beds = bedRepository.findAll();

        Map<Long, List<BedSelectResponse>> bedsByRoom = beds.stream()
                .collect(Collectors.groupingBy(
                        bed -> bed.getRoom().getId(),
                        Collectors.mapping(
                                bed -> new BedSelectResponse(
                                        bed.getId(),
                                        bed.getBedNumber()),
                                Collectors.toList())));

        Map<Long, List<RoomSelectResponse>> roomsByFacility = rooms.stream()
                .collect(Collectors.groupingBy(
                        room -> room.getFacility().getId(),
                        Collectors.mapping(
                                room -> new RoomSelectResponse(
                                        room.getId(),
                                        room.getRoomNumber(),
                                        bedsByRoom.getOrDefault(room.getId(), List.of())),
                                Collectors.toList())));

        return facilities.stream()
                .map(facility -> new FacilitySelectResponse(
                        facility.getId(),
                        facility.getName(),
                        roomsByFacility.getOrDefault(facility.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public FacilityResponse updateFacilityInfo(Long facilityId, FacilityUpdateRequest request) {
        FacilityEntity facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found"));

        facilityMapper.updateEntity(facility, request);
        facility = facilityRepository.save(facility);

        return facilityMapper.toResponse(facility);
    }
}
