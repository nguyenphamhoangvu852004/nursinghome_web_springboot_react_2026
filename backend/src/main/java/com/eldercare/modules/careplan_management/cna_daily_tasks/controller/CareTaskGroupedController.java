package com.eldercare.modules.careplan_management.cna_daily_tasks.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.*;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.EnrichedTaskRow;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByCnaCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByResidentCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.TaskDetailDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.service.CareTaskGroupedService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CareTaskGroupedController {

    private final CareTaskGroupedService careTaskGroupedService;

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @GetMapping(RouteConstants.API_TASKS_BY_CNA)
    public ResponseEntity<PagedResponse<List<GroupedByCnaCard>>> getTasksByCna(@Valid @ModelAttribute GroupedTaskQuery query) {
        return ResponseEntity.ok(careTaskGroupedService.getTasksByCna(query));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @GetMapping(RouteConstants.API_TASKS_BY_RESIDENT)
    public ResponseEntity<PagedResponse<List<GroupedByResidentCard>>> getTasksByResident(@Valid @ModelAttribute GroupedTaskQuery query) {
        return ResponseEntity.ok(careTaskGroupedService.getTasksByResident(query));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @GetMapping(RouteConstants.API_TASKS_SEARCH)
    public ResponseEntity<PagedResponse<List<EnrichedTaskRow>>> searchTasks(@ModelAttribute TaskSearchFilter filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        return ResponseEntity.ok(careTaskGroupedService.searchTasks(filter, pageable));
    }

    @PreAuthorize("hasRole('NHA_ADMIN') or hasRole('CNA')")
    @GetMapping(RouteConstants.API_INTERVENTION_TASKS)
    public ResponseEntity<PagedResponse<List<TaskDetailDto>>> listTasksByIntervention(
            @PathVariable Long interventionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(careTaskGroupedService.listTasksByIntervention(interventionId, pageable));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PostMapping(RouteConstants.API_INTERVENTION_TASKS)
    public ResponseEntity<ApiResponse<TaskDetailDto>> createTask(
            @PathVariable Long interventionId,
            @Valid @RequestBody CreateTaskRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Task created successfully", careTaskGroupedService.createTask(interventionId, request)));
    }

    @PreAuthorize("hasRole('NHA_ADMIN') or hasRole('CNA')")
    @GetMapping(RouteConstants.API_TASK_BY_ID)
    public ResponseEntity<ApiResponse<TaskDetailDto>> getTaskDetail(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.success("Task detail fetched successfully", careTaskGroupedService.getTaskDetail(taskId)));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PutMapping(RouteConstants.API_TASK_BY_ID)
    public ResponseEntity<ApiResponse<TaskDetailDto>> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", careTaskGroupedService.updateTask(taskId, request)));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @DeleteMapping(RouteConstants.API_TASK_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long taskId) {
        careTaskGroupedService.deleteTask(taskId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PatchMapping(RouteConstants.API_TASK_ASSIGN_CNA)
    public ResponseEntity<ApiResponse<TaskDetailDto>> assignCna(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignCnaRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("CNA assigned successfully", careTaskGroupedService.assignCna(taskId, request.getAssignedCnaId())));
    }

    @PreAuthorize("hasRole('NHA_ADMIN') or hasRole('CNA')")
    @PatchMapping(RouteConstants.API_TASK_COMPLETED)
    public ResponseEntity<ApiResponse<TaskDetailDto>> completeTask(
            @PathVariable Long taskId,
            @Valid @RequestBody CompleteTaskRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Task completed successfully", careTaskGroupedService.completeTask(taskId, request.getCompletedAt())));
    }

    @PreAuthorize("hasRole('NHA_ADMIN') or hasRole('CNA')")
    @PatchMapping(RouteConstants.API_TASK_MISSED)
    public ResponseEntity<ApiResponse<TaskDetailDto>> markMissed(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.success("Task marked missed successfully", careTaskGroupedService.markMissed(taskId)));
    }

    @PreAuthorize("hasRole('NHA_ADMIN') or hasRole('CNA')")
    @PatchMapping(RouteConstants.API_TASK_FLAG_ABNORMAL)
    public ResponseEntity<ApiResponse<TaskDetailDto>> flagAbnormal(
            @PathVariable Long taskId,
            @Valid @RequestBody FlagAbnormalRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Task abnormality updated successfully", careTaskGroupedService.flagAbnormal(taskId, request.getIsAbnormalFlagged())));
    }

    @PreAuthorize("hasRole('NHA_ADMIN')")
    @PatchMapping(RouteConstants.API_TASK_RESCHEDULE)
    public ResponseEntity<ApiResponse<TaskDetailDto>> rescheduleTask(
            @PathVariable Long taskId,
            @Valid @RequestBody RescheduleTaskRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Task rescheduled successfully", careTaskGroupedService.rescheduleTask(taskId, request.getScheduledTime())));
    }
}
