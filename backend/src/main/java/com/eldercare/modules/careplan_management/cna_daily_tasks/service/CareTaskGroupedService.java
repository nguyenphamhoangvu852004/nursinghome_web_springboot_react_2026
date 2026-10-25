package com.eldercare.modules.careplan_management.cna_daily_tasks.service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.CreateTaskRequestDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.GroupedTaskQuery;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.TaskSearchFilter;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.UpdateTaskRequestDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByCnaCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByResidentCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.TaskDetailDto;
import org.springframework.data.domain.Pageable;
import java.time.OffsetDateTime;
import java.util.List;

public interface CareTaskGroupedService {
    PagedResponse<List<GroupedByCnaCard>> getTasksByCna(GroupedTaskQuery query);
    PagedResponse<List<GroupedByResidentCard>> getTasksByResident(GroupedTaskQuery query);
    PagedResponse<List<EnrichedTaskRow>> searchTasks(TaskSearchFilter filter, Pageable pageable);
    
    TaskDetailDto createTask(Long interventionId, CreateTaskRequestDto request);
    TaskDetailDto updateTask(Long taskId, UpdateTaskRequestDto request);
    void deleteTask(Long taskId);
    PagedResponse<List<TaskDetailDto>> listTasksByIntervention(Long interventionId, Pageable pageable);
    TaskDetailDto getTaskDetail(Long taskId);
    TaskDetailDto assignCna(Long taskId, Long assignedCnaId);
    TaskDetailDto completeTask(Long taskId, OffsetDateTime completedAt);
    TaskDetailDto markMissed(Long taskId);
    TaskDetailDto flagAbnormal(Long taskId, Boolean isAbnormalFlagged);
    TaskDetailDto rescheduleTask(Long taskId, OffsetDateTime scheduledTime);
}
