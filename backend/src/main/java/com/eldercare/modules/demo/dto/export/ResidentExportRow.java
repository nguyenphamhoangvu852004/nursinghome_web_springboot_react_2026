package com.eldercare.modules.demo.dto.export;

public record ResidentExportRow(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        String dateOfBirth,
        String gender,
        String maritalStatus,
        String religionPreference,
        String status,
        Boolean chartLocked,
        Long bedId,
        String createdAt
) {
}
