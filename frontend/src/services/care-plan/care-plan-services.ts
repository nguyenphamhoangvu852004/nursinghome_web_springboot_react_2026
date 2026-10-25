import { CARE_PLAN_API_BASE_URL } from "@/common/config/api-url";
import { apiClient } from "@/lib/api-client";
import type {
  GetCarePlanDetailResponse,
  GetCarePlanListResponse,
} from "./care-plan-types";

export type GetCarePlanListParams = {
  page?: number;
  size?: number;
  residentId?: number;
  status?: string;
  significantChangeFlag?: boolean;
  sortBy?: string;
  sortDir?: "ASC" | "DESC";
};

export const getCarePlanList = async (
  params?: GetCarePlanListParams,
): Promise<GetCarePlanListResponse> => {
  const response = await apiClient.get<GetCarePlanListResponse>("/legacy/care-plans", {
    params,
    baseURL: CARE_PLAN_API_BASE_URL,
  });

  return response.data;
};

export const getCarePlanDetail = async (id: number) => {
  const response = await apiClient.get<GetCarePlanDetailResponse>(
    `/legacy/care-plans/${id}`,
    { baseURL: CARE_PLAN_API_BASE_URL },
  );

  return response.data;
};

export type ApproveCarePlanResponse = {
  id: number;
  status: "ACTIVE";
  updatedAt: string;
};

export const approveCarePlan = async (
  id: number,
): Promise<ApproveCarePlanResponse> => {
  const response = await apiClient.patch(`/legacy/care-plans/${id}/activate`, undefined, { baseURL: CARE_PLAN_API_BASE_URL });

  return response.data.data;
};

export const searchCarePlans = async (
  resident?: string,
  status?:
    | "Draft"
    | "Active"
    | "Need update"
    | "Review due"
    | "Archived"
    | "Pending review"
    | "All",
): Promise<GetCarePlanListResponse> => {
  if (status === "All") {
    return getCarePlanList();
  }

  const params = new URLSearchParams();

  if (resident) {
    params.append("residentName", resident);
  }

  if (status) {
    params.append("status", status.toUpperCase().replaceAll(" ", "_"));
  }

  const response = await apiClient.get<GetCarePlanListResponse>(
    `/legacy/care-plans/search?${params.toString()}`,
    { baseURL: CARE_PLAN_API_BASE_URL },
  );

  return response.data;
};
