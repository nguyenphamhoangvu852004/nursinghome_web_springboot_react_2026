package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResidentDetailResponseDto {
    private Long id;
    private String name;
    private String initials;
    private String room;
    private String status;
    private LocalDate dob;
    private Integer age;
    private List<Badge> badges;
    private Demographics demographics;
    private Poa poa;
    private List<String> diagnoses;
    private List<String> allergies;
    private Insurance insurance;
    private LocSummary locSummary;

    @Getter
    @Setter
    public static class Badge {
        private String text;
        private String type;

        public Badge() {}

        public Badge(String text, String type) {
            this.text = text;
            this.type = type;
        }
    }

    @Getter
    @Setter
    public static class Demographics {
        private String legalName;
        private String address;
        private LocalDate dob;
        private LocalDate admissionDate;
        private String ssn;
        private String roomBed;
        private String gender;
        private String referralSource;
        private String maritalStatus;
        private String emergencyContact;
        private String phone;
        private String payerSource;
    }

    @Getter
    @Setter
    public static class Poa {
        private String name;
        private String relationship;
        private String contact;
        private String dnrFlag;
    }

    @Getter
    @Setter
    public static class Insurance {
        private String medicareNum;
        private String provider;
        private String payerName;
        private String authNum;
        private String authStartEnd;
    }

    @Getter
    @Setter
    public static class LocSummary {
        private String level;
        private String adlScore;
    }
}
