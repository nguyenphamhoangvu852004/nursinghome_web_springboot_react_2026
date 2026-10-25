package com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO;

import com.eldercare.modules.resident_intake.resident.dto.response.ResidentResponseDto;

import java.util.Map;

public class CarePlanOutput {

    public int id;
    public String status;
    public Boolean significantFlag;
    public CarePlanResidentOutput resident;
    public CarePlanResidentDefinitionOutput definition;
    public int goalCount;
    public int interventionCount;
    public int LOCTier;
    public String lastReviewedBy;
    public String lastReviewedDateTime;
    public String nextReviewDateTime;
    public CarePlanAuthorOutput createdBy;
    public int cycle;
    public String createdAt;
    public String updatedAt;
    public Boolean isDeleted;

    public static class CarePlanResidentOutput {
        public int id;
        public String fullname;
        public String dateOfBirth;

        public CarePlanResidentOutput(int id, String fullname, String dateOfBirth) {
            this.id = id;
            this.fullname = fullname;
            this.dateOfBirth = dateOfBirth;
        }
    }

    public static class CarePlanResidentDefinitionOutput {
        public String room;
        public String bed;

        public CarePlanResidentDefinitionOutput(String room, String bed) {
            this.room = room;
            this.bed = bed;
        }
    }

    public static class CarePlanAuthorOutput {
        public int id;
        public String fullname;
        public String role;

        public CarePlanAuthorOutput(int id, String fullname, String role) {
            this.id = id;
            this.fullname = fullname;
            this.role = role;
        }

    }

}