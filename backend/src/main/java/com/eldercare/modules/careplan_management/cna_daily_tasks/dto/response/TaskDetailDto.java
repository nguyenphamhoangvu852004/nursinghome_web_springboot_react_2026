package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailDto {
    private Long id;
    private String taskType;
    private TaskStatus status;
    private Boolean isAbnormalFlagged;
    private Long careInterventionId;
    private Long assignedCnaId;
    private OffsetDateTime scheduledTime;
    private OffsetDateTime completedAt;
    private String goal;
}
