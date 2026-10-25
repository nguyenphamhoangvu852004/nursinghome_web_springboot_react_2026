import apiClient from "@/common/config/api";


export type SeverityLevel = { id: number; level_name: string; chart_lock_trigger: boolean };

export const incidentSeverityApi = {
  getAll: () => apiClient.get<SeverityLevel[]>("/incident-severity-levels").then((res) => res.data),
};