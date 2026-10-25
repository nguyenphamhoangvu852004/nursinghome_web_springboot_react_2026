package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class ResidentInfoResponseDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String religionPreference;
    private String status;
    private Boolean isChartLocked;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private AddressDto address;
    private BedDto bed;

    @Data
    @Builder
    public static class AddressDto {
        private Long id;
        private String streetLine1;
        private String streetLine2;
        private String city;
        private String state;
        private String zipCode;
        private String addressType;
    }

    @Data
    @Builder
    public static class BedDto {
        private Long id;
        private String bedNumber;
        private String status;
        private Long roomId;
        private String roomNumber;
        private String roomType;
        private Long facilityId;
        private String facilityName;
    }
}
