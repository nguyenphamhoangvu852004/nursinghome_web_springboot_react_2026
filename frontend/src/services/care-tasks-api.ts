import { apiClient } from "@/lib/api-client";
import type { ApiResponse, PagedApiResponse as PagedResponse } from "@/types/api";

export interface EnrichedTaskRow {
  id: number;
  careInterventionId: number;
  carePlanId: number;
  residentId: number;
  residentDisplayName: string;
  roomNumber: string;
  assignedCnaId: number | null;
  assignedCnaDisplayName: string | null;
  scheduledTime: string;
  completedAt: string | null;
  taskType: string;
  status: string;
  isAbnormalFlagged: boolean;
  goal: string;
}

export interface GroupedByCnaCard {
  cnaId: number | null;
  cnaDisplayName: string | null;
  totalTasks: number;
  completedTasks: number;
  missedTasks: number;
  tasks: EnrichedTaskRow[];
}

export interface GroupedByResidentCard {
  residentId: number;
  residentDisplayName: string;
  roomNumber: string;
  carePlanId: number;
  totalTasks: number;
  completedTasks: number;
  missedTasks: number;
  tasks: EnrichedTaskRow[];
}

export interface TaskDetailDto {
  id: number;
  taskType: string;
  status: string;
  isAbnormalFlagged: boolean;
  careInterventionId: number | null;
  assignedCnaId: number | null;
  scheduledTime: string;
  completedAt: string | null;
  goal: string | null;
}

/** Query params for GET /tasks/by-cna and GET /tasks/by-resident */
export interface GroupedTaskQueryParams {
  date?: string;
  status?: string;
  taskType?: string;
  residentId?: number;
  assignedCnaId?: number;
  isAbnormalFlagged?: boolean;
  page?: number;
  size?: number;
}

/** Query params for GET /tasks/search */
export interface TaskSearchParams {
  fromDate?: string;
  toDate?: string;
  status?: string;
  taskType?: string;
  residentId?: number;
  assignedCnaId?: number;
  isAbnormalFlagged?: boolean;
  page?: number;
  size?: number;
}

export const careTasksApi = {
  getTasksByCna: async (params?: GroupedTaskQueryParams): Promise<PagedResponse<GroupedByCnaCard[]>> => {
    const { data } = await apiClient.get<PagedResponse<GroupedByCnaCard[]>>("/tasks/by-cna", { params });
    return data;
  },

  getTasksByResident: async (params?: GroupedTaskQueryParams): Promise<PagedResponse<GroupedByResidentCard[]>> => {
    const { data } = await apiClient.get<PagedResponse<GroupedByResidentCard[]>>("/tasks/by-resident", { params });
    return data;
  },

  searchTasks: async (params?: TaskSearchParams): Promise<PagedResponse<EnrichedTaskRow[]>> => {
    const { data } = await apiClient.get<PagedResponse<EnrichedTaskRow[]>>("/tasks/search", { params });
    return data;
  },

  completeTask: async (taskId: string | number, completedAt?: string): Promise<TaskDetailDto> => {
    const { data } = await apiClient.patch<ApiResponse<TaskDetailDto>>(`/tasks/${taskId}/completed`, { completedAt });
    return data.data;
  },

  rescheduleTask: async (taskId: string | number, scheduledTime: string): Promise<TaskDetailDto> => {
    const { data } = await apiClient.patch<ApiResponse<TaskDetailDto>>(`/tasks/${taskId}/reschedule`, { scheduledTime });
    return data.data;
  },

  markMissed: async (taskId: string | number): Promise<TaskDetailDto> => {
    const { data } = await apiClient.patch<ApiResponse<TaskDetailDto>>(`/tasks/${taskId}/missed`);
    return data.data;
  },

  flagAbnormal: async (taskId: string | number, isAbnormalFlagged: boolean): Promise<TaskDetailDto> => {
    const { data } = await apiClient.patch<ApiResponse<TaskDetailDto>>(`/tasks/${taskId}/flag-abnormal`, { isAbnormalFlagged });
    return data.data;
  },

  assignCna: async (taskId: string | number, assignedCnaId: number | null): Promise<TaskDetailDto> => {
    const { data } = await apiClient.patch<ApiResponse<TaskDetailDto>>(`/tasks/${taskId}/assign-cna`, { assignedCnaId });
    return data.data;
  },

  deleteTask: async (taskId: string | number): Promise<void> => {
    await apiClient.delete(`/tasks/${taskId}`);
  },

  updateTask: async (
    taskId: string | number,
    payload: { taskType?: string; assignedCnaId?: number | null; scheduledTime?: string; goal?: string }
  ): Promise<TaskDetailDto> => {
    const { data } = await apiClient.put<ApiResponse<TaskDetailDto>>(`/tasks/${taskId}`, payload);
    return data.data;
  },

  createTask: async (
    interventionId: string | number,
    payload: { taskType: string; assignedCnaId?: number | null; scheduledTime: string; goal?: string }
  ): Promise<TaskDetailDto> => {
    const { data } = await apiClient.post<ApiResponse<TaskDetailDto>>(`/interventions/${interventionId}/tasks`, payload);
    return data.data;
  },
};
