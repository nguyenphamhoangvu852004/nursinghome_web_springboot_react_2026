package com.eldercare.modules.admin.facility_setup.facility.facility_layout.mapper;

import com.eldercare.common.enums.BedStatus;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.BedRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.RoomRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.BedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.RoomResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.BedEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.entity.RoomEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_layout.repository.BedRepository.BedProjection;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RoomBedMapper {

    public RoomResponse toResponse(RoomEntity room, List<BedProjection> enrichedBeds) {
        if (room == null) return null;

        List<BedResponse> bedResponses = enrichedBeds.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        int capacity = bedResponses.size();
        int occupiedCount = (int) bedResponses.stream()
                .filter(b -> BedStatus.OCCUPIED.name().equals(b.getStatus() != null ? b.getStatus().name() : null))
                .count();

        String status;
        if (capacity == 0 || occupiedCount == 0) {
            status = "Available";
        } else if (occupiedCount == capacity) {
            status = "Full";
        } else {
            status = occupiedCount + "/" + capacity + " Occupied";
        }

        RoomResponse res = new RoomResponse();
        res.setId(room.getId());
        res.setRoomNumber(room.getRoomNumber());
        res.setRoomType(room.getRoomType());
        res.setFacilityId(room.getFacility() != null ? room.getFacility().getId() : null);
        res.setFacilityName(room.getFacility() != null ? room.getFacility().getName() : null);
        res.setBeds(bedResponses);
        res.setCapacity(capacity);
        res.setOccupiedCount(occupiedCount);
        res.setStatus(status);
        return res;
    }

    public List<RoomResponse> toRoomResponseList(List<RoomEntity> rooms, List<BedProjection> allEnrichedBeds) {
        // Group enriched beds by roomId for O(1) lookup
        Map<Long, List<BedProjection>> bedsByRoomId = allEnrichedBeds.stream()
                .collect(Collectors.groupingBy(BedProjection::getRoomId));

        return rooms.stream()
                .map(room -> toResponse(room, bedsByRoomId.getOrDefault(room.getId(), List.of())))
                .collect(Collectors.toList());
    }

    public BedResponse toResponse(BedProjection projection) {
        if (projection == null) return null;
        BedResponse res = new BedResponse();
        res.setId(projection.getId());
        res.setBedNumber(projection.getBedNumber());
        res.setStatus(projection.getStatus() != null ? BedStatus.valueOf(projection.getStatus()) : null);
        res.setRoomId(projection.getRoomId());
        res.setResidentName(projection.getResidentName());
        res.setAdmissionDate(projection.getAdmissionDate());
        return res;
    }

    public RoomEntity toEntity(RoomRequest request) {
        if (request == null) return null;
        RoomEntity room = new RoomEntity();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(request.getRoomType());
        return room;
    }

    public BedResponse toResponse(BedEntity bed) {
        if (bed == null) return null;
        BedResponse res = new BedResponse();
        res.setId(bed.getId());
        res.setBedNumber(bed.getBedNumber());
        res.setStatus(bed.getStatus());
        res.setRoomId(bed.getRoom() != null ? bed.getRoom().getId() : null);
        return res;
    }

    public List<BedResponse> toBedResponseList(List<BedEntity> beds) {
        return beds.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BedEntity toEntity(BedRequest request) {
        if (request == null) return null;
        BedEntity bed = new BedEntity();
        bed.setBedNumber(request.getBedNumber());
        bed.setStatus(request.getStatus());
        return bed;
    }
}
