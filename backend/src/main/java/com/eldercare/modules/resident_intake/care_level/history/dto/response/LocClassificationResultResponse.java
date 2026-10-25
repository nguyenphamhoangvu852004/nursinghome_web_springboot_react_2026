package com.eldercare.modules.resident_intake.care_level.history.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class LocClassificationResultResponse {

    private Long assessmentId;

    private Long residentId;
    private String residentName;

    private OffsetDateTime assessmentDate;

    private Integer adlTotalScore;

    private Long suggestedCareLevelId;
    private String suggestedCareLevelCode;
    private String suggestedCareLevelName;

    private Long confirmedCareLevelId;
    private String confirmedCareLevelCode;
    private String confirmedCareLevelName;

    private Boolean overridden;

    private String assessedBy;

    private List<AssessmentItemResponse> details;
}