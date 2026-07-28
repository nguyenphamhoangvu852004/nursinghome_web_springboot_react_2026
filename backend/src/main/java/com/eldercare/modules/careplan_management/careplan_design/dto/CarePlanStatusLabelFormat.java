package com.eldercare.modules.careplan_management.careplan_design.dto;

import com.eldercare.modules.careplan_management.careplan_design.entity.CarePlanEntity;

public class CarePlanStatusLabelFormat {
    public static String getLabel(CarePlanEntity entity) {
        switch (entity.getStatus()) {
            case DRAFT:
                return "Draft";
            case PENDING_REVIEW:
                return "Pending Review";

            case ACTIVE:
                return "Active";

            case REVIEW_DUE:
                return "Review due";

            case ARCHIVED:
                return "Archived";

            case NEEDS_UPDATE:
                return "Need update";

            default:
                return "Invalid state" + entity.getStatus().toString();
        }
    }
}
