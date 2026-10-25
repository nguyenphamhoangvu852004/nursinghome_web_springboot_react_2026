package com.eldercare.modules.resident_intake.care_level.history.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransitionResidentCareLevelRequest {

    @NotNull(message = "Care level is required")
    private Long careLevelId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

}