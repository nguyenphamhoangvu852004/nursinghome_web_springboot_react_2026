package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ResidentContactResponseDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String relationshipType;
    private Boolean isGuarantor;
    private Boolean isEmergencyContact;
    private Boolean isPrimary;
    private BigDecimal financialResponsibilityPct;
    private String phonePrimary;
    private String phoneSecondary;
    private String email;
    private AddressDto address;

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
}
