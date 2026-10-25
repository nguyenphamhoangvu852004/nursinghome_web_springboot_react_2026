package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResidentSensitiveInfoResponseDto {
    private Long id;
    private Long residentId;
    private String ssnEncrypted;
    private String medicalRecordNumberEncrypted;
    private String primaryInsuranceIdEncrypted;
    private String bankAccountEncrypted;
}
