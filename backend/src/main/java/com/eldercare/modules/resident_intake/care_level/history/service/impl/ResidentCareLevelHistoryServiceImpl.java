package com.eldercare.modules.resident_intake.care_level.history.service.impl;

import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelEntity;
import com.eldercare.modules.admin.facility_setup.care_level.entity.ResidentCareLevelHistoryEntity;
import com.eldercare.modules.admin.facility_setup.care_level.repository.CareLevelRepository;
import com.eldercare.modules.resident_intake.care_level.history.dto.request.TransitionResidentCareLevelRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.request.UpdateResidentCareLevelHistoryRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.ActiveCareLevelSummaryResponse;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.ResidentCareLevelHistoryResponse;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.TransitionResidentCareLevelResponse;
import com.eldercare.modules.resident_intake.care_level.history.service.ResidentCareLevelHistoryService;
import com.eldercare.modules.resident_intake.resident.repository.ResidentCareLevelHistoryRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResidentCareLevelHistoryServiceImpl
        implements ResidentCareLevelHistoryService {


    private final ResidentCareLevelHistoryRepository historyRepository;
    private final ResidentRepository residentRepository;
    private final CareLevelRepository careLevelRepository;



    // ==========================================================
    // API 23
    // GET /residents/{residentId}/care-level-history
    // ==========================================================
    @Override
    public List<ResidentCareLevelHistoryResponse> getResidentCareLevelHistory(
            Long residentId
    ) {

        if (!residentRepository.existsById(residentId)) {
            throw new RuntimeException(
                    "Resident not found with id: " + residentId
            );
        }


        return historyRepository
                .findByResident_IdOrderByStartDateDesc(residentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // ==========================================================
    // API 24
    // POST /residents/{residentId}/care-level-history
    // ==========================================================
    @Override
    @Transactional
    public TransitionResidentCareLevelResponse transitionResidentCareLevel(
            Long residentId,
            TransitionResidentCareLevelRequest request
    ) {


        ResidentEntity resident =
                residentRepository.findById(residentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resident not found with id: "
                                                + residentId
                                )
                        );


        CareLevelEntity careLevel =
                careLevelRepository.findById(request.getCareLevelId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Care level not found with id: "
                                                + request.getCareLevelId()
                                )
                        );


        ResidentCareLevelHistoryEntity current =
                historyRepository
                        .findByResident_IdAndEndDateIsNull(residentId)
                        .orElse(null);



        if (current != null) {


            if (!request.getStartDate()
                    .isAfter(current.getStartDate())) {

                throw new RuntimeException(
                        "startDate must be after current startDate"
                );
            }


            current.setEndDate(request.getStartDate());

            historyRepository.save(current);
        }



        ResidentCareLevelHistoryEntity newRecord =
                ResidentCareLevelHistoryEntity.builder()
                        .resident(resident)
                        .careLevel(careLevel)
                        .startDate(request.getStartDate())
                        .endDate(null)
                        .action("CONFIRMED")
                        .build();



        ResidentCareLevelHistoryEntity saved =
                historyRepository.save(newRecord);



        return TransitionResidentCareLevelResponse.builder()
                .newRecord(
                        mapToResponse(saved)
                )
                .closedRecord(
                        current == null
                                ? null
                                : mapToResponse(current)
                )
                .build();
    }




    // ==========================================================
    // API 25
    // GET /care-level-history/active-summary
    // ==========================================================
    @Override
    public List<ActiveCareLevelSummaryResponse>
    getCareLevelActiveSummary() {


        Map<String, Long> summary =
                historyRepository
                        .findByEndDateIsNull()
                        .stream()
                        .collect(Collectors.groupingBy(
                                history ->
                                        history.getCareLevel()
                                                .getLevelCode(),
                                Collectors.counting()
                        ));



        return summary.entrySet()
                .stream()
                .map(entry ->
                        ActiveCareLevelSummaryResponse.builder()
                                .levelCode(entry.getKey())
                                .activeResidentCount(
                                        entry.getValue()
                                )
                                .build()
                )
                .toList();
    }




    // ==========================================================
    // API 26
    // PATCH /care-level-history/{id}
    // ==========================================================
    @Override
    @Transactional
    public ResidentCareLevelHistoryResponse updateCareLevelHistory(
            Long historyId,
            UpdateResidentCareLevelHistoryRequest request
    ) {


        ResidentCareLevelHistoryEntity history =
                historyRepository.findById(historyId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "History not found: "
                                                + historyId
                                )
                        );



        // Không cho sửa record hiện tại
        if (history.getEndDate() == null) {

            throw new RuntimeException(
                    "Cannot edit current care level history. "
                    + "Use transition API instead."
            );
        }



        // Reason bắt buộc theo spec
        if (request.getReason() == null ||
                request.getReason().trim().isEmpty()) {

            throw new RuntimeException(
                    "Reason is required"
            );
        }



        if (request.getCareLevelId() != null) {

            CareLevelEntity careLevel =
                    careLevelRepository
                            .findById(request.getCareLevelId())
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Care level not found"
                                    )
                            );


            history.setCareLevel(careLevel);
        }



        if (request.getStartDate() != null) {

            history.setStartDate(
                    request.getStartDate()
            );
        }



        return mapToResponse(
                historyRepository.save(history)
        );
    }





    // ==========================================================
    // API 27
    // DELETE blocked
    // ==========================================================
    @Override
public void deleteCareLevelHistory(Long historyId) {

    if (!historyRepository.existsById(historyId)) {
        throw new RuntimeException(
                "Care level history not found with id: " + historyId
        );
    }


}




    // ==========================================================
    // Mapper
    // ==========================================================
    private ResidentCareLevelHistoryResponse mapToResponse(
            ResidentCareLevelHistoryEntity entity
    ) {


        return ResidentCareLevelHistoryResponse.builder()
                .id(entity.getId())
                .careLevelId(
                        entity.getCareLevel().getId()
                )
                .levelCode(
                        entity.getCareLevel().getLevelCode()
                )
                .startDate(
                        entity.getStartDate()

                )
                .action(entity.getAction())       
                .endDate(
                        entity.getEndDate()
                )
                .build();
    }

}