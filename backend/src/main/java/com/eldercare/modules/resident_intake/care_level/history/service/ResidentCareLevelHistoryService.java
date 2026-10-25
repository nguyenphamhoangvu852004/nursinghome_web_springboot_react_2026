package com.eldercare.modules.resident_intake.care_level.history.service;

import com.eldercare.modules.resident_intake.care_level.history.dto.request.TransitionResidentCareLevelRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.request.UpdateResidentCareLevelHistoryRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.ActiveCareLevelSummaryResponse;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.ResidentCareLevelHistoryResponse;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.TransitionResidentCareLevelResponse;

import java.util.List;

public interface ResidentCareLevelHistoryService {

    /**
     * API 23
     * Returns the full history of care level changes for a resident.
     */
    List<ResidentCareLevelHistoryResponse> getResidentCareLevelHistory(
            Long residentId
    );


    /**
     * API 24
     * Creates a new history record and closes the current record.
     */
    TransitionResidentCareLevelResponse transitionResidentCareLevel(
            Long residentId,
            TransitionResidentCareLevelRequest request
    );


    /**
     * API 25
     * Count of active residents per care level.
     */
    List<ActiveCareLevelSummaryResponse> getCareLevelActiveSummary();


    /**
     * API 26
     * Updates a non-current care level history record.
     */
    ResidentCareLevelHistoryResponse updateCareLevelHistory(
            Long historyId,
            UpdateResidentCareLevelHistoryRequest request
    );


    /**
     * API 27
     * Care level history cannot be deleted.
     */
    void deleteCareLevelHistory(Long historyId);

}