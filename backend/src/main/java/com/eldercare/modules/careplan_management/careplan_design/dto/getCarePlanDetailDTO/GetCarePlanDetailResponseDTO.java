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
}
