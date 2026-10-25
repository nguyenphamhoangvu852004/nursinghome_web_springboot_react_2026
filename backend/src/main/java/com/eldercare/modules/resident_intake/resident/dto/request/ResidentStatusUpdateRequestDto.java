package com.eldercare.modules.resident_intake.resident.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ResidentStatusUpdateRequestDto {
    private String status;
    private String reason;
    private LocalDate effectiveDate;
}
