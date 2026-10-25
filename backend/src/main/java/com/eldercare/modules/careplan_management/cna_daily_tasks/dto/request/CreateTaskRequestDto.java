package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequestDto {
    @NotNull(message = "Task type cannot be null")
    private String taskType;

    private Long assignedCnaId;
    @Size(max = 2000)
    private String goal;

    @NotNull(message = "Scheduled time cannot be null")
    private OffsetDateTime scheduledTime;
}
