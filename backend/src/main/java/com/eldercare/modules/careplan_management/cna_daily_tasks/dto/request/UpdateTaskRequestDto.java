package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateTaskRequestDto {
    @Size(max = 50)
    private String taskType;
    private Long assignedCnaId;
    private OffsetDateTime scheduledTime;
    @Size(max = 2000)
    private String goal;
}
