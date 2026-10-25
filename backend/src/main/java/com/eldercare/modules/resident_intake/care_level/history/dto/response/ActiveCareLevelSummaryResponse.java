package com.eldercare.modules.resident_intake.care_level.history.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveCareLevelSummaryResponse {

    private String levelCode;

    private Long activeResidentCount;

}