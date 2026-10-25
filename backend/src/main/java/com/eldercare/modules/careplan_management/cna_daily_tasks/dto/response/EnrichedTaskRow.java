package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedTaskRow {
    private Long id;
    private Long careInterventionId;
    private Long carePlanId;
    private Long residentId;
    private String residentDisplayName;
    private String roomNumber;
    private Long assignedCnaId;
    private String assignedCnaDisplayName;
    private OffsetDateTime scheduledTime;
    private OffsetDateTime completedAt;
    private String taskType;
    private String status;
    private Boolean isAbnormalFlagged;
    private String goal;
}
