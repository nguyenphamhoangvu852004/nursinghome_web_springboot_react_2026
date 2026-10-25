package com.eldercare.modules.resident_intake.care_level.history.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateResidentCareLevelHistoryRequest {

    @NotNull(message = "Care level is required")
    private Long careLevelId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotBlank(message = "Reason is required")
    private String reason;

}