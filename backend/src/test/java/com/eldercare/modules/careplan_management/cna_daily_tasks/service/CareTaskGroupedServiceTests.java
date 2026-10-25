package com.eldercare.modules.careplan_management.cna_daily_tasks.service;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.entity.CareInterventionEntity;
import com.eldercare.modules.careplan_management.careplan_design.repository.JpaCareInterventionRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.request.CreateTaskRequestDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.dto.response.TaskDetailDto;
import com.eldercare.modules.careplan_management.cna_daily_tasks.entity.CareTaskEntity;
import com.eldercare.modules.careplan_management.cna_daily_tasks.enums.TaskStatus;
import com.eldercare.modules.careplan_management.cna_daily_tasks.mapper.CareTaskGroupedMapper;
import com.eldercare.modules.careplan_management.cna_daily_tasks.repository.CareTaskRepository;
import com.eldercare.modules.careplan_management.cna_daily_tasks.service.impl.CareTaskGroupedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CareTaskGroupedService - Equivalence Partitioning Tests")
class CareTaskGroupedServiceTests {

    @Mock
    private CareTaskRepository careTaskRepository;

    @Mock
    private CareTaskGroupedMapper careTaskGroupedMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JpaCareInterventionRepository careInterventionRepository;

    @InjectMocks
    private CareTaskGroupedServiceImpl careTaskGroupedService;

    private CareTaskEntity pendingTask;
    private CareTaskEntity completedTask;
    private CareTaskEntity missedTask;

    @BeforeEach
    void setUp() {
        pendingTask = new CareTaskEntity();
        pendingTask.setId(1L);
        pendingTask.setStatus(TaskStatus.PENDING);

        completedTask = new CareTaskEntity();
        completedTask.setId(2L);
        completedTask.setStatus(TaskStatus.COMPLETED);

        missedTask = new CareTaskEntity();
        missedTask.setId(3L);
        missedTask.setStatus(TaskStatus.MISSED);
    }

    @Nested
    @DisplayName("completeTask - Equivalence Partitioning")
    class CompleteTaskTests {
        @Test
        @DisplayName("EP1: Task is PENDING -> Successfully completes the task")
        void completeTask_PendingTask_CompletesSuccessfully() {
            // Arrange
            when(careTaskRepository.findById(1L)).thenReturn(Optional.of(pendingTask));
            when(careTaskRepository.save(any(CareTaskEntity.class))).thenReturn(pendingTask);

            // Act
            TaskDetailDto result = careTaskGroupedService.completeTask(1L, OffsetDateTime.now());

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(TaskStatus.COMPLETED);
            verify(careTaskRepository).save(pendingTask);
        }

        @Test
        @DisplayName("EP2: Task is COMPLETED -> Throws IllegalArgumentException")
        void completeTask_CompletedTask_ThrowsException() {
            // Arrange
            when(careTaskRepository.findById(2L)).thenReturn(Optional.of(completedTask));

            // Act & Assert
            assertThatThrownBy(() -> careTaskGroupedService.completeTask(2L, OffsetDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot complete a finalized task");
        }

        @Test
        @DisplayName("EP3: Task not found -> Throws NotFoundException")
        void completeTask_NotFound_ThrowsException() {
            // Arrange
            when(careTaskRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> careTaskGroupedService.completeTask(99L, OffsetDateTime.now()))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("Task not found with id: 99");
        }
    }

    @Nested
    @DisplayName("rescheduleTask - Equivalence Partitioning")
    class RescheduleTaskTests {
        @Test
        @DisplayName("EP1: Task is PENDING -> Updates scheduled time")
        void rescheduleTask_Pending_UpdatesTime() {
            // Arrange
            OffsetDateTime newTime = OffsetDateTime.now().plusHours(1);
            when(careTaskRepository.findById(1L)).thenReturn(Optional.of(pendingTask));
            when(careTaskRepository.save(any(CareTaskEntity.class))).thenReturn(pendingTask);

            // Act
            TaskDetailDto result = careTaskGroupedService.rescheduleTask(1L, newTime);

            // Assert
            assertThat(result).isNotNull();
            verify(careTaskRepository).save(pendingTask);
            assertThat(pendingTask.getScheduledTime()).isEqualTo(newTime);
        }

        @Test
        @DisplayName("EP2: Task is MISSED -> Creates new task")
        void rescheduleTask_Missed_CreatesNewTask() {
            // Arrange
            OffsetDateTime newTime = OffsetDateTime.now().plusHours(1);
            when(careTaskRepository.findById(3L)).thenReturn(Optional.of(missedTask));
            
            CareTaskEntity savedNewTask = new CareTaskEntity();
            savedNewTask.setId(4L);
            savedNewTask.setStatus(TaskStatus.PENDING);
            savedNewTask.setScheduledTime(newTime);
            
            when(careTaskRepository.save(any(CareTaskEntity.class))).thenReturn(savedNewTask);

            // Act
            TaskDetailDto result = careTaskGroupedService.rescheduleTask(3L, newTime);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(4L);
            assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
            
            // Verify the original task wasn't modified directly in terms of status/time before save
            // (the save is called on the NEW task)
            verify(careTaskRepository).save(argThat(task -> task.getId() == null && task.getStatus() == TaskStatus.PENDING));
        }

        @Test
        @DisplayName("EP3: Task is COMPLETED -> Throws exception")
        void rescheduleTask_Completed_ThrowsException() {
            // Arrange
            when(careTaskRepository.findById(2L)).thenReturn(Optional.of(completedTask));

            // Act & Assert
            assertThatThrownBy(() -> careTaskGroupedService.rescheduleTask(2L, OffsetDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot reschedule a COMPLETED task");
        }
    }

    @Nested
    @DisplayName("createTask - Equivalence Partitioning")
    class CreateTaskTests {
        @Test
        @DisplayName("EP1: Valid intervention and no CNA -> Creates unassigned task")
        void createTask_ValidIntervention_NoCna_CreatesUnassignedTask() {
            // Arrange
            CreateTaskRequestDto request = new CreateTaskRequestDto();
            request.setAssignedCnaId(null);
            
            CareInterventionEntity intervention = new CareInterventionEntity();
            intervention.setId(1);

            when(careInterventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
            when(careTaskRepository.save(any(CareTaskEntity.class))).thenAnswer(i -> {
                CareTaskEntity task = i.getArgument(0);
                task.setId(10L);
                return task;
            });

            // Act
            TaskDetailDto result = careTaskGroupedService.createTask(1L, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getAssignedCnaId()).isNull();
            verify(userRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("EP2: Valid intervention and valid CNA -> Creates assigned task")
        void createTask_ValidIntervention_ValidCna_CreatesAssignedTask() {
            // Arrange
            CreateTaskRequestDto request = new CreateTaskRequestDto();
            request.setAssignedCnaId(5L);
            
            CareInterventionEntity intervention = new CareInterventionEntity();
            intervention.setId(1);

            UserEntity cna = new UserEntity();
            cna.setId(5L);

            when(careInterventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
            when(userRepository.findById(5L)).thenReturn(Optional.of(cna));
            when(careTaskRepository.save(any(CareTaskEntity.class))).thenAnswer(i -> {
                CareTaskEntity task = i.getArgument(0);
                task.setId(10L);
                return task;
            });

            // Act
            TaskDetailDto result = careTaskGroupedService.createTask(1L, request);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getAssignedCnaId()).isEqualTo(5L);
            verify(userRepository).findById(5L);
        }

        @Test
        @DisplayName("EP3: Intervention not found -> Throws NotFoundException")
        void createTask_InterventionNotFound_ThrowsException() {
            // Arrange
            CreateTaskRequestDto request = new CreateTaskRequestDto();
            when(careInterventionRepository.findById(99L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> careTaskGroupedService.createTask(99L, request))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("CareIntervention not found");
        }
    }
}
