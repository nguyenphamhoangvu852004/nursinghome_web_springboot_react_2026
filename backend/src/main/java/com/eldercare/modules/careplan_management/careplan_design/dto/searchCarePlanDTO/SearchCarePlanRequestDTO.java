package com.eldercare.modules.careplan_management.careplan_design.dto.searchCarePlanDTO;

import com.eldercare.common.enums.CarePlanStatusEnum;
import org.springframework.data.domain.Sort;

public class SearchCarePlanRequestDTO {
    public String keyword;
    public String residentName;
    public CarePlanStatusEnum status;
    public Boolean significantChangeFlag;

    public int page = 0;
    public int size = 20;

    public String sortBy = "updatedAt";
    public Sort.Direction sortDir = Sort.Direction.DESC;
}
