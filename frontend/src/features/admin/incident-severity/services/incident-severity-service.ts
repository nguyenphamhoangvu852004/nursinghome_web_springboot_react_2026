import apiClient from "@/common/config/api";

export type IncidentSeverityResponse = {
  id: number;
  levelName: string;
  chartLockTrigger: boolean;
  description?: string | null;
  example?: string | null;
};

type IncidentSeverityApiResponse = {
  id: number;
  level_name: string;
  chart_lock_trigger: boolean;
  description?: string | null;
  example?: string | null;
};

export type CreateIncidentSeverityRequest = {
  level_name: string;
  chart_lock_trigger: boolean;
  description?: string;
  example?: string;
};

export type UpdateIncidentSeverityRequest = {
  level_name: string;
  chart_lock_trigger: boolean;
  description?: string;
  example?: string;
};

export const createIncidentSeverityLevel = async (
  request: CreateIncidentSeverityRequest
): Promise<IncidentSeverityResponse> => {
  const response = await apiClient.post<IncidentSeverityApiResponse>(
    "/incident-severity-levels",
    request
  );

  return {
    id: response.data.id,
    levelName: response.data.level_name,
    chartLockTrigger: response.data.chart_lock_trigger,
    description: response.data.description,
    example: response.data.example,
  };
};

export const fetchIncidentSeverityLevels = async (): Promise<IncidentSeverityResponse[]> => {
  const response = await apiClient.get<IncidentSeverityApiResponse[]>("/incident-severity-levels");
  console.log("API response incident severity levels:", response.data);

  return response.data.map((item) => ({
    id: item.id,
    levelName: item.level_name,
    chartLockTrigger: item.chart_lock_trigger,
    description: item.description,
    example: item.example,
  }));
};

export const updateIncidentSeverityLevel = async (
  severityId: number,
  request: UpdateIncidentSeverityRequest
): Promise<IncidentSeverityResponse> => {
  const response = await apiClient.put<IncidentSeverityApiResponse>(
    `/incident-severity-levels/${severityId}`,
    request
  );

  return {
    id: response.data.id,
    levelName: response.data.level_name,
    chartLockTrigger: response.data.chart_lock_trigger,
    description: response.data.description,
    example: response.data.example,
  };
};
