package com.eldercare.modules.careplan_management.careplan_design.dto.v2.ListCarePlans;

import java.util.List;

public record CarePlanListData(List<CarePlanListItem> list, Statistics statistics) {
    public record Statistics(long total, long open, long draft, long pendingApproval, long effective) { }
}
