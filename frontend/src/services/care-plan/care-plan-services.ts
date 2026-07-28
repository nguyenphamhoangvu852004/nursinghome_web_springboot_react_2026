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
  const response = await apiClient.get<GetCarePlanListResponse>("/care-plans", {
    params,
  });

  return response.data;
};

export const getCarePlanDetail = async (id: number) => {
  const response = await apiClient.get<GetCarePlanDetailResponse>(
    `/care-plans/${id}`,
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
  const response = await apiClient.patch(`/care-plans/${id}/activate`);

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
    `/care-plans/search?${params.toString()}`,
  );

  return response.data;
};
