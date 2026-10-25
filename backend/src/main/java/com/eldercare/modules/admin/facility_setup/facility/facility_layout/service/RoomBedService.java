package com.eldercare.modules.admin.facility_setup.facility.facility_layout.service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.BedRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.request.RoomRequest;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.BedResponse;
import com.eldercare.modules.admin.facility_setup.facility.dto.response.RoomResponse;

import java.util.List;

public interface RoomBedService {
    PagedResponse<List<RoomResponse>> getRoomList(Long facilityId, int page, int size, String search);
    PagedResponse<List<RoomResponse>> getAllRooms(int page, int size, String search);
    RoomResponse createRoom(Long facilityId, RoomRequest request);
    RoomResponse updateRoom(Long roomId, RoomRequest request);
    void deleteRoom(Long roomId);

    List<BedResponse> getBedListByRoomId(Long roomId);
    BedResponse createBed(Long roomId, BedRequest request);
    BedResponse updateBedStatus(Long bedId, BedRequest request);
    void deleteBed(Long bedId);
}
