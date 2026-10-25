package com.eldercare.modules.careplan_management.cna_daily_tasks.service.impl;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaCareInterventionRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.CreateTaskRequestDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.GroupedTaskQuery;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.TaskSearchFilter;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.UpdateTaskRequestDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByCnaCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByResidentCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.TaskDetailDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import com.eldercare.modules.careplan_management.cna_daily_tasks.mapper.CareTaskGroupedMapper;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.service.CareTaskGroupedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareTaskGroupedServiceImpl implements CareTaskGroupedService {

    private final CareTaskRepository careTaskRepository;
    private final CareTaskGroupedMapper careTaskGroupedMapper;
    private final UserRepository userRepository;
    private final JpaCareInterventionRepository careInterventionRepository;

    @Override
    public PagedResponse<List<GroupedByCnaCard>> getTasksByCna(GroupedTaskQuery query) {
        List<EnrichedTaskRow> allTasks = fetchTasksForGrouping(query);

        Map<Long, List<EnrichedTaskRow>> groupedByCnaId = allTasks.stream()
                .collect(Collectors.groupingBy(task -> task.getAssignedCnaId() != null ? task.getAssignedCnaId() : -1L));

        List<GroupedByCnaCard> cards = groupedByCnaId.entrySet().stream()
                .map(entry -> careTaskGroupedMapper.toGroupedByCnaCard(
                        entry.getKey() == -1L ? null : entry.getKey(), 
                        entry.getValue()
                ))
                .collect(Collectors.toList());

        return createPagedResponse(cards, query.getPage(), query.getSize());
    }

    @Override
    public PagedResponse<List<GroupedByResidentCard>> getTasksByResident(GroupedTaskQuery query) {
        List<EnrichedTaskRow> allTasks = fetchTasksForGrouping(query);

        Map<Long, List<EnrichedTaskRow>> groupedByResidentId = allTasks.stream()
                .collect(Collectors.groupingBy(EnrichedTaskRow::getResidentId));

        List<GroupedByResidentCard> cards = groupedByResidentId.entrySet().stream()
                .map(entry -> careTaskGroupedMapper.toGroupedByResidentCard(
                        entry.getKey(), 
                        entry.getValue()
                ))
                .collect(Collectors.toList());

        return createPagedResponse(cards, query.getPage(), query.getSize());
    }

    private List<EnrichedTaskRow> fetchTasksForGrouping(GroupedTaskQuery query) {
        OffsetDateTime startOfDay = query.getDate() != null ? query.getDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime endOfDay = query.getDate() != null ? query.getDate().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC) : null;

        return careTaskRepository.findEnrichedTasksForGrouping(
                startOfDay, endOfDay, query.getStatus(), query.getTaskType(),
                query.getResidentId(), query.getAssignedCnaId(), query.getIsAbnormalFlagged()
        );
    }

    private <T> PagedResponse<List<T>> createPagedResponse(List<T> data, int page, int size) {
        int start = Math.min(page * size, data.size());
        int end = Math.min((start + size), data.size());
        List<T> pagedCards = data.subList(start, end);
        
        int totalPages = (int) Math.ceil((double) data.size() / size);
        
        return PagedResponse.of(
            pagedCards,
            200,
            "Success",
            page,
            totalPages,
            size,
            data.size()
        );
    }

    @Override
    public PagedResponse<List<EnrichedTaskRow>> searchTasks(TaskSearchFilter filter, Pageable pageable) {
        OffsetDateTime startOfDay = filter.getFromDate() != null ? filter.getFromDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null;
        OffsetDateTime endOfDay = filter.getToDate() != null ? filter.getToDate().plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC) : null;

        Page<EnrichedTaskRow> page = careTaskRepository.searchEnrichedTasks(
                startOfDay, endOfDay, filter.getStatus(), filter.getTaskType(),
                filter.getResidentId(), filter.getAssignedCnaId(), filter.getIsAbnormalFlagged(), pageable
        );
        return PagedResponse.of(
            page.getContent(),
            200,
            "Success",
            page.getNumber(),
            page.getTotalPages(),
            page.getSize(),
            page.getTotalElements()
        );
    }
    
    private TaskDetailDto mapToDetailDto(CareTaskEntity task) {
        return TaskDetailDto.builder()
                .id(task.getId())
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .isAbnormalFlagged(task.getIsAbnormalFlagged())
                .careInterventionId(task.getCareIntervention() != null ? task.getCareIntervention().getId() : null)
                .assignedCnaId(task.getAssignedCna() != null ? task.getAssignedCna().getId() : null)
                .scheduledTime(task.getScheduledTime())
                .completedAt(task.getCompletedAt())
                .goal(task.getGoal())
                .build();
    }

    @Override
    @Transactional
    public TaskDetailDto createTask(Long interventionId, CreateTaskRequestDto request) {
        CareInterventionEntity intervention = careInterventionRepository.findById(interventionId)
                .orElseThrow(() -> new NotFoundException("CareIntervention not found with id: " + interventionId));

        UserEntity cna = null;
        if (request.getAssignedCnaId() != null) {
            cna = userRepository.findById(request.getAssignedCnaId())
                    .orElseThrow(() -> new NotFoundException("CNA not found with id: " + request.getAssignedCnaId()));
        }

        CareTaskEntity task = new CareTaskEntity();
        task.setTaskType(request.getTaskType());
        task.setStatus(TaskStatus.PENDING);
        task.setIsAbnormalFlagged(false);
        task.setCareIntervention(intervention);
        task.setAssignedCna(cna);
        task.setGoal(request.getGoal());
        task.setScheduledTime(request.getScheduledTime());
        
        CareTaskEntity savedTask = careTaskRepository.save(task);
        return mapToDetailDto(savedTask);
    }

    @Override
    @Transactional
    public TaskDetailDto updateTask(Long taskId, UpdateTaskRequestDto request) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
                
        if (request.getTaskType() != null) {
            task.setTaskType(request.getTaskType());
        }
        if (request.getScheduledTime() != null) {
            task.setScheduledTime(request.getScheduledTime());
        }
        if (request.getGoal() != null) {
            task.setGoal(request.getGoal());
        }
        
        // We explicitly check for null because if it's sent in the update DTO, we might want to unassign it
        // However, a common pattern for "don't update if null" vs "update to null" requires a way to distinguish.
        // Assuming the DTO might come with assignedCnaId = null if it was explicitly sent:
        // Wait, normally `assignedCnaId` is a Long. Let's assume if it's not null it updates. 
        // For unassigning, they should use `PATCH /api/v1/tasks/{taskId}/assign-cna` with null.
        if (request.getAssignedCnaId() != null) {
            UserEntity cna = userRepository.findById(request.getAssignedCnaId())
                    .orElseThrow(() -> new NotFoundException("CNA not found with id: " + request.getAssignedCnaId()));
            task.setAssignedCna(cna);
        }

        careTaskRepository.save(task);
        return mapToDetailDto(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long taskId) {
        if (!careTaskRepository.existsById(taskId)) {
            throw new NotFoundException("Task not found with id: " + taskId);
        }
        careTaskRepository.deleteById(taskId);
    }

    @Override
    public PagedResponse<List<TaskDetailDto>> listTasksByIntervention(Long interventionId, Pageable pageable) {
        Page<CareTaskEntity> page = careTaskRepository.findByCareIntervention_Id(interventionId, pageable);
        List<TaskDetailDto> dtos = page.getContent().stream()
                .map(this::mapToDetailDto)
                .collect(Collectors.toList());
                
        return PagedResponse.of(
            dtos,
            200,
            "Success",
            page.getNumber(),
            page.getTotalPages(),
            page.getSize(),
            page.getTotalElements()
        );
    }
    
    @Override
    public TaskDetailDto getTaskDetail(Long taskId) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
        return mapToDetailDto(task);
    }

    @Override
    @Transactional
    public TaskDetailDto assignCna(Long taskId, Long assignedCnaId) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
                
        if (assignedCnaId == null) {
            task.setAssignedCna(null);
        } else {
            UserEntity cna = userRepository.findById(assignedCnaId)
                    .orElseThrow(() -> new NotFoundException("CNA not found with id: " + assignedCnaId));
            task.setAssignedCna(cna);
        }
        
        careTaskRepository.save(task);
        return mapToDetailDto(task);
    }

    @Override
    @Transactional
    public TaskDetailDto completeTask(Long taskId, OffsetDateTime completedAt) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
                
        if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.MISSED) {
            throw new IllegalArgumentException("Cannot complete a finalized task");
        }
        
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(completedAt != null ? completedAt : OffsetDateTime.now());
        
        careTaskRepository.save(task);
        return mapToDetailDto(task);
    }

    @Override
    @Transactional
    public TaskDetailDto markMissed(Long taskId) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
                
        if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.MISSED) {
            throw new IllegalArgumentException("Cannot mark missed on a finalized task");
        }
        
        task.setStatus(TaskStatus.MISSED);
        
        careTaskRepository.save(task);
        return mapToDetailDto(task);
    }

    @Override
    @Transactional
    public TaskDetailDto flagAbnormal(Long taskId, Boolean isAbnormalFlagged) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
                
        task.setIsAbnormalFlagged(isAbnormalFlagged);
        
        careTaskRepository.save(task);
        return mapToDetailDto(task);
    }

    @Override
    @Transactional
    public TaskDetailDto rescheduleTask(Long taskId, OffsetDateTime scheduledTime) {
        CareTaskEntity task = careTaskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
                
        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot reschedule a COMPLETED task");
        }
        
        if (task.getStatus() == TaskStatus.MISSED) {
            // Create a new task with PENDING status
            CareTaskEntity newTask = new CareTaskEntity();
            newTask.setTaskType(task.getTaskType());
            newTask.setStatus(TaskStatus.PENDING);
            newTask.setIsAbnormalFlagged(task.getIsAbnormalFlagged());
            newTask.setCareIntervention(task.getCareIntervention());
            newTask.setAssignedCna(task.getAssignedCna());
            newTask.setScheduledTime(scheduledTime);
            newTask.setGoal(task.getGoal());
            
            CareTaskEntity savedNewTask = careTaskRepository.save(newTask);
            return mapToDetailDto(savedNewTask);
        }
        
        // If PENDING, just update existing
        task.setScheduledTime(scheduledTime);
        careTaskRepository.save(task);
        return mapToDetailDto(task);
    }
}
