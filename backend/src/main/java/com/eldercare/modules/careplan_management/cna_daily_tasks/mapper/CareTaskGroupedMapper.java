package com.eldercare.modules.careplan_management.cna_daily_tasks.mapper;

import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByCnaCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByResidentCard;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CareTaskGroupedMapper {

    public GroupedByCnaCard toGroupedByCnaCard(Long cnaId, List<EnrichedTaskRow> tasks) {
        String cnaName = tasks.isEmpty() ? null : (cnaId == null ? "Unassigned" : tasks.get(0).getAssignedCnaDisplayName());
        
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        int missed = (int) tasks.stream().filter(t -> "MISSED".equals(t.getStatus())).count();

        return GroupedByCnaCard.builder()
                .cnaId(cnaId)
                .cnaDisplayName(cnaName)
                .totalTasks(total)
                .completedTasks(completed)
                .missedTasks(missed)
                .tasks(tasks)
                .build();
    }

    public GroupedByResidentCard toGroupedByResidentCard(Long residentId, List<EnrichedTaskRow> tasks) {
        EnrichedTaskRow firstTask = tasks.get(0);
        
        int total = tasks.size();
        int completed = (int) tasks.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
        int missed = (int) tasks.stream().filter(t -> "MISSED".equals(t.getStatus())).count();

        return GroupedByResidentCard.builder()
                .residentId(residentId)
                .residentDisplayName(firstTask.getResidentDisplayName())
                .roomNumber(firstTask.getRoomNumber())
                .carePlanId(firstTask.getCarePlanId())
                .totalTasks(total)
                .completedTasks(completed)
                .missedTasks(missed)
                .tasks(tasks)
                .build();
    }
}
