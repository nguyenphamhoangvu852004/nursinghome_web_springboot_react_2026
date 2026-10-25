package com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO;

import org.springframework.data.domain.Sort;

import com.eldercare.common.enums.CarePlanStatusEnum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ListCarePlanRequestDTO {
    public Integer page = 0;
    public Integer size = 20;

    public Long residentId;

    public String residentName;

    public CarePlanStatusEnum status;

    public Boolean significantChangeFlag;

    public String sortBy = "updatedAt";

    public Sort.Direction sortDir = Sort.Direction.DESC;

}
