package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlagAbnormalRequestDto {
    @NotNull
    private Boolean isAbnormalFlagged;
}
