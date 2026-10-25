import apiClient from "@/common/config/api";

export type IncidentApiResponse = {
  id: number;
  resident: {
    id: number;
    displayName: string;
    gender: string;
    bed?: { id: number; bedNumber: string; room?: { id: number; roomNumber: string; roomType: string } };
  };
  incidentType: "FALL" | "MEDICATION_ERROR" | "ALTERCATION" | "SKIN_TEAR";
  status: "OPEN" | "UNDER_INVESTIGATION" | "SUBMITTED" | "RESOLVED";
  automaticLockChart: boolean;
  location?: string;
  description?: string;
  witnesses?: string;
  slaDeadlineHours?: number;
  severity: { id: number; levelName: string; slaConfigured?: number };
  reporter?: { id: number; displayName: string };
  isLocked: boolean;
  reportedAt: string;
  slaCountDown?: number;
  timelines: { id: number; action: string; actor: { id: number; displayName: string }; createdAt: string }[];
  createdAt: string;
};

export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

export const incidentsApi = {
  getAll: (page = 0, size = 10) =>
    apiClient.get<PageResponse<IncidentApiResponse>>("/incidents", { params: { page, size } })
       .then((res) => res.data),

  getById: (id: number) =>
    apiClient.get<IncidentApiResponse>(`/incidents/${id}`).then((res) => res.data),

  create: (payload: {
    residentID: number;
    incidentType: IncidentApiResponse["incidentType"];
    severityID: number;
    occurredAt: string; // "yyyy-MM-dd HH:mm:ss"
    location?: string;
    description?: string;
    witnesses?: string;
  }) => apiClient.post<IncidentApiResponse>("/incidents", payload).then((res) => res.data),
};