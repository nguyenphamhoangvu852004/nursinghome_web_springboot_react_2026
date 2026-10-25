package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResidentListResponseItemDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String currentCareLevel;
}
