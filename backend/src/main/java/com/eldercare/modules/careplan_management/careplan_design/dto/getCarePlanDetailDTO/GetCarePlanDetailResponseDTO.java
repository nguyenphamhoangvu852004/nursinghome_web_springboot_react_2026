package com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO;

import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.CarePlanOutput;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.CarePlanOutput.CarePlanAuthorOutput;

import java.util.List;

public class GetCarePlanDetailResponseDTO {
    public int id;
    public String status;
    public int locTier;
    public Boolean significantFlag;
    public CarePlanOutput.CarePlanResidentOutput resident;
    public CarePlanOutput.CarePlanResidentDefinitionOutput definition;
    public String lastReviewedBy;
    public String lastReviewedDateTime;
    public String nextReviewDateTime;
    public int cycle;
    public List<Goal> goals;
    public CarePlanAuthorOutput createdBy;
    public String createdAt;
    public String updatedAt;
    public Boolean isDeleted;
    public CostEstimation costEstimation;

    public static class Goal {
        public int id;
        public String title;
        public String goalDescription;
        public String status;
        public List<Intervention> interventions;
    }

    public static class Intervention {
        public int id;
        public String title;
        public String assignedRole;
    }

    public static class CostEstimation {
        public String locTierRatePerDay;
        public String locTierRatePerMonth;
        public String locTierRate;
        public String bedRate;

        public CostEstimation(String locTierRatePerDay, String locTierRatePerMonth, String locTierRate,
                String bedRate) {

            this.locTierRatePerDay = locTierRatePerDay;
            this.locTierRatePerMonth = locTierRatePerMonth;
            this.locTierRate = locTierRate;
            this.bedRate = bedRate;
        }

    }
}
