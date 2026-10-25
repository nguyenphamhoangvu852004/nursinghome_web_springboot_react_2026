package com.eldercare.modules.resident_intake.care_level.history.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidentCareLevelHistoryResponse {

    private Long id;

    private Long careLevelId;

    private String levelCode;

    private LocalDate startDate;

    private String action;
    
    private LocalDate endDate;

}