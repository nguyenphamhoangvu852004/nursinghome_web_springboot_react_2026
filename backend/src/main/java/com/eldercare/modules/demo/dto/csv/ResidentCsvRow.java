package com.eldercare.modules.demo.dto.csv;

public record ResidentCsvRow(
        String firstName,
        String middleName,
        String lastName,
        String dateOfBirth,
        String gender,
        String maritalStatus,
        String religionPreference,
        String status,
        String facilityCode,
        String roomNumber,
        String bedNumber,
        String careLevelCode,
        int medicationCount,
        int incidentCount
) {
}
