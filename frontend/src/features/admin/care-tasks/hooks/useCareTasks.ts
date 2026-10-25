import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { careTasksApi } from "@/services/care-tasks-api";
import { userService } from "@/services/user-service";
import type { GroupedTaskQueryParams } from "@/services/care-tasks-api";

export function useCareTasks(params?: GroupedTaskQueryParams) {
  const queryClient = useQueryClient();

  const { data: cnaGroupsData, isLoading: isLoadingCnaGroups, error: errorCnaGroups } = useQuery({
    queryKey: ["care-tasks-by-cna", params],
    queryFn: () => careTasksApi.getTasksByCna(params),
  });

  const { data: residentGroupsData, isLoading: isLoadingResidentGroups, error: errorResidentGroups } = useQuery({
    queryKey: ["care-tasks-by-resident", params],
    queryFn: () => careTasksApi.getTasksByResident(params),
  });

  const invalidateQueries = () => {
    queryClient.invalidateQueries({ queryKey: ["care-tasks-by-cna"] });
    queryClient.invalidateQueries({ queryKey: ["care-tasks-by-resident"] });
  };

  const completeTaskMutation = useMutation({
    mutationFn: ({ taskId, completedAt }: { taskId: string | number; completedAt?: string }) => 
      careTasksApi.completeTask(taskId, completedAt),
    onSuccess: invalidateQueries,
  });

  const rescheduleTaskMutation = useMutation({
    mutationFn: ({ taskId, scheduledTime }: { taskId: string | number; scheduledTime: string }) => 
      careTasksApi.rescheduleTask(taskId, scheduledTime),
    onSuccess: invalidateQueries,
  });

  const markMissedMutation = useMutation({
    mutationFn: (taskId: string | number) => careTasksApi.markMissed(taskId),
    onSuccess: invalidateQueries,
  });

  const flagAbnormalMutation = useMutation({
    mutationFn: ({ taskId, isAbnormalFlagged }: { taskId: string | number; isAbnormalFlagged: boolean }) => 
      careTasksApi.flagAbnormal(taskId, isAbnormalFlagged),
    onSuccess: invalidateQueries,
  });

  const assignCnaMutation = useMutation({
    mutationFn: ({ taskId, assignedCnaId }: { taskId: string | number; assignedCnaId: number | null }) => 
      careTasksApi.assignCna(taskId, assignedCnaId),
    onSuccess: invalidateQueries,
  });

  const deleteTaskMutation = useMutation({
    mutationFn: (taskId: string | number) => careTasksApi.deleteTask(taskId),
    onSuccess: invalidateQueries,
  });

  const updateTaskMutation = useMutation({
    mutationFn: ({ taskId, payload }: { taskId: string | number; payload: { taskType?: string; assignedCnaId?: number | null; scheduledTime?: string; goal?: string } }) => 
      careTasksApi.updateTask(taskId, payload),
    onSuccess: invalidateQueries,
  });

  return {
    cnaGroupsData,
    isLoading: isLoadingCnaGroups,
    residentGroupsData,
    isLoadingResidents: isLoadingResidentGroups,
    error: errorCnaGroups?.message || errorResidentGroups?.message || null,
    
    completeTask: completeTaskMutation.mutateAsync,
    isCompleting: completeTaskMutation.isPending,

    rescheduleTask: rescheduleTaskMutation.mutateAsync,
    isRescheduling: rescheduleTaskMutation.isPending,

    markMissed: markMissedMutation.mutateAsync,
    isMarkingMissed: markMissedMutation.isPending,

    flagAbnormal: flagAbnormalMutation.mutateAsync,
    isFlaggingAbnormal: flagAbnormalMutation.isPending,

    assignCna: assignCnaMutation.mutateAsync,
    isAssigningCna: assignCnaMutation.isPending,

    deleteTask: deleteTaskMutation.mutateAsync,
    isDeleting: deleteTaskMutation.isPending,

    updateTask: updateTaskMutation.mutateAsync,
    isUpdating: updateTaskMutation.isPending,
  };
}

export function useActiveCnas() {
  return useQuery({
    queryKey: ["active-cnas"],
    queryFn: () => userService.getActiveCnas(),
  });
}
