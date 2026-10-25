package com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO;

import java.util.List;

public class ListCarePlanResponseDTO {
    public List<CarePlanOutput> list;

    public ListCarePlanResponseDTO(List<CarePlanOutput> listCarePlanOutput) {
        this.list = listCarePlanOutput;
    }
    
}
