package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GroupedTaskQuery {
    private LocalDate date;
    private String status;
    private String taskType;
    private Long residentId;
    private Long assignedCnaId;
    private Boolean isAbnormalFlagged;
    private int page = 0;
    private int size = 10;
}
