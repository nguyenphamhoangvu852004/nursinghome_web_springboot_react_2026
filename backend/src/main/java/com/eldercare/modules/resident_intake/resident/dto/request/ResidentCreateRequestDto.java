package com.eldercare.modules.resident_intake.resident.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ResidentCreateRequestDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String religionPreference;
    private Long addressId;
}
