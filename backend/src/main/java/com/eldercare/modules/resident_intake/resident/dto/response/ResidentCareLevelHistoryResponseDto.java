package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ResidentCareLevelHistoryResponseDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long careLevelId;
    private String careLevelCode;
    private String careLevelName;
}
