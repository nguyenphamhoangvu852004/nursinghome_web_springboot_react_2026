package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CompleteTaskRequestDto {
    private OffsetDateTime completedAt;
}
