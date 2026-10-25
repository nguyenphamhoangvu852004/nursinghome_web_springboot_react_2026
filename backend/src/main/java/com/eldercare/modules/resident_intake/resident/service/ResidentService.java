package com.eldercare.modules.resident_intake.resident.service;

import com.eldercare.modules.resident_intake.resident.dto.request.*;
import com.eldercare.modules.resident_intake.resident.dto.response.*;
import java.util.List;

public interface ResidentService {
    // Existing operations
    List<ResidentListResponseDto> getResidents(String search, String status, String referral);

    ResidentDetailResponseDto getResidentDetail(Long id);

    void saveResident(Long id, ResidentSaveRequestDto dto);

    void createResident(ResidentSaveRequestDto dto);

    void deleteResident(Long id);

    // New versioned operations (endpoints 10-18)
    ResidentListResponseContainerDto getResidentsV1(String status, Long bedId, String search, Integer page,
            Integer pageSize);

    ResidentResponseDto getResidentByIdV1(Long id);

    ResidentResponseDto createResidentV1(ResidentCreateRequestDto dto);

    ResidentResponseDto updateResidentV1(Long id, ResidentUpdateRequestDto dto);

    ResidentResponseDto updateResidentStatusV1(Long id, ResidentStatusUpdateRequestDto dto);

    ResidentResponseDto assignResidentBedV1(Long id, ResidentAssignBedRequestDto dto);

    ResidentResponseDto lockResidentChartV1(Long id, ResidentChartLockRequestDto dto);

    ResidentResponseDto unlockResidentChartV1(Long id, ResidentChartLockRequestDto dto);

    ResidentInfoResponseDto getResidentInfo(Long id);
    List<ResidentContactResponseDto> getResidentContacts(Long id);
    List<ResidentCareLevelHistoryResponseDto> getResidentCareLevelHistory(Long id);
    ResidentSensitiveInfoResponseDto getResidentSensitiveInfo(Long id);

    List<ResidentPendingDTO> getPendingResidents();
}
