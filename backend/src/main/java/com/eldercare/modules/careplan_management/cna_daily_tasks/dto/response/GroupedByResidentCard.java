package com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupedByResidentCard {
    private Long residentId;
    private String residentDisplayName;
    private String roomNumber;
    private Long carePlanId;
    private int totalTasks;
    private int completedTasks;
    private int missedTasks;
    private List<EnrichedTaskRow> tasks;
}
