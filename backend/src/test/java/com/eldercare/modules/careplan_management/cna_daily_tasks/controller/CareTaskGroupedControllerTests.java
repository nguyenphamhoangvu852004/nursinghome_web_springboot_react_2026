package com.eldercare.modules.careplan_management.cna_daily_tasks.controller;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.CreateTaskRequestDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.GroupedTaskQuery;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.GroupedByCnaCard;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.TaskDetailDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.service.CareTaskGroupedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CareTaskGroupedController - Equivalence Partitioning Tests")
class CareTaskGroupedControllerTests {

    private MockMvc mockMvc;

    @Mock
    private CareTaskGroupedService careTaskGroupedService;

    @InjectMocks
    private CareTaskGroupedController careTaskGroupedController;

    private TaskDetailDto taskDetailDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(careTaskGroupedController).build();

        taskDetailDto = TaskDetailDto.builder()
                .id(1L)
                .assignedCnaId(5L)
                .build();
    }

    @Nested
    @DisplayName("GET " + RouteConstants.API_TASKS_BY_CNA)
    class GetTasksByCnaTests {
        @Test
        @DisplayName("EP1: Valid request -> Returns 200 OK")
        void getTasksByCna_ValidRequest_ReturnsOk() throws Exception {
            PagedResponse<List<GroupedByCnaCard>> response = PagedResponse.of(
                    List.of(new GroupedByCnaCard()), 200, "Success", 0, 1, 10, 1);
            
            when(careTaskGroupedService.getTasksByCna(any(GroupedTaskQuery.class))).thenReturn(response);

            mockMvc.perform(get(RouteConstants.API_TASKS_BY_CNA)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
                    
            verify(careTaskGroupedService).getTasksByCna(any(GroupedTaskQuery.class));
        }
    }

    @Nested
    @DisplayName("GET " + RouteConstants.API_TASK_BY_ID)
    class GetTaskDetailTests {
        @Test
        @DisplayName("EP1: Valid Task ID -> Returns 200 OK")
        void getTaskDetail_ValidId_ReturnsOk() throws Exception {
            when(careTaskGroupedService.getTaskDetail(1L)).thenReturn(taskDetailDto);

            mockMvc.perform(get(RouteConstants.API_TASK_BY_ID.replace("{taskId}", "1"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1L));
        }
    }

    @Nested
    @DisplayName("POST " + RouteConstants.API_INTERVENTION_TASKS)
    class CreateTaskTests {
        @Test
        @DisplayName("EP1: Valid request body -> Returns 201 Created")
        void createTask_ValidRequest_ReturnsCreated() throws Exception {
            when(careTaskGroupedService.createTask(eq(1L), any(CreateTaskRequestDto.class))).thenReturn(taskDetailDto);

            String requestBody = "{\"taskType\":\"BATHING\", \"scheduledTime\":\"2026-07-16T10:00:00Z\"}";

            mockMvc.perform(post(RouteConstants.API_INTERVENTION_TASKS.replace("{interventionId}", "1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.id").value(1L));
        }
    }

    @Nested
    @DisplayName("PATCH " + RouteConstants.API_TASK_COMPLETED)
    class CompleteTaskTests {
        @Test
        @DisplayName("EP1: Valid request -> Returns 200 OK")
        void completeTask_ValidRequest_ReturnsOk() throws Exception {
            when(careTaskGroupedService.completeTask(eq(1L), any())).thenReturn(taskDetailDto);

            String requestBody = "{}";

            mockMvc.perform(patch(RouteConstants.API_TASK_COMPLETED.replace("{taskId}", "1"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1L));
        }
    }

    @Nested
    @DisplayName("DELETE " + RouteConstants.API_TASK_BY_ID)
    class DeleteTaskTests {
        @Test
        @DisplayName("EP1: Valid Task ID -> Returns 200 OK")
        void deleteTask_ValidId_ReturnsOk() throws Exception {
            mockMvc.perform(delete(RouteConstants.API_TASK_BY_ID.replace("{taskId}", "1"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
                    
            verify(careTaskGroupedService).deleteTask(1L);
        }
    }
}
