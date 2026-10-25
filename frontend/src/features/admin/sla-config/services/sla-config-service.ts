import apiClient from "@/common/config/api";

export type SLAConfigResponse = {
  id: number;
  severityId: number;
  slaWindowHrs: number;
  externalReportRequired?: boolean;
  regulatoryBody?: string;
};

type SLAConfigApiResponse = {
  id: number;
  severity_id: number;
  sla_window_hrs: number;
  external_report_required?: boolean;
  regulatory_body?: string;
};

export type CreateSLAConfigRequest = {
  severity_id: number;
  sla_window_hrs: number;
  external_report_required?: boolean;
  regulatory_body?: string;
};

export type UpdateSLAConfigRequest = {
  severity_id: number;
  sla_window_hrs: number;
  external_report_required?: boolean;
  regulatory_body?: string;
};

export const createSLAConfig = async (
  request: CreateSLAConfigRequest
): Promise<SLAConfigResponse> => {
  const response = await apiClient.post<SLAConfigApiResponse>(
    "/admin/sla-configs",
    request
  );

  return {
    id: response.data.id,
    severityId: response.data.severity_id,
    slaWindowHrs: response.data.sla_window_hrs,
    externalReportRequired: response.data.external_report_required,
    regulatoryBody: response.data.regulatory_body,
  };
};

export const fetchSLAConfigs = async (): Promise<SLAConfigResponse[]> => {
  const response = await apiClient.get<SLAConfigApiResponse[]>("/admin/sla-configs");
  console.log("SLA API response:", response.data);
  const mapped = response.data.map((item) => ({
    id: item.id,
    severityId: item.severity_id,
    slaWindowHrs: item.sla_window_hrs,
    externalReportRequired: item.external_report_required,
    regulatoryBody: item.regulatory_body,
  }));
  console.log("SLA API mapped response:", mapped);
  return mapped;
};

export const updateSLAConfig = async (
  slaConfigId: number,
  request: UpdateSLAConfigRequest
): Promise<SLAConfigResponse> => {
  const response = await apiClient.put<SLAConfigApiResponse>(
    `/admin/sla-configs/${slaConfigId}`,
    request
  );

  return {
    id: response.data.id,
    severityId: response.data.severity_id,
    slaWindowHrs: response.data.sla_window_hrs,
    externalReportRequired: response.data.external_report_required,
    regulatoryBody: response.data.regulatory_body,
  };
};
