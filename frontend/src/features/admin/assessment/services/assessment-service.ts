import { axiosInstance } from "@/lib/axios";
import type { ApiResponse, PagedApiResponse } from "@/types/api";
import type {
  AssessmentCreateRequest,
  AssessmentDecisionRequest,
  AssessmentMetricDTO,
  AssessmentResponse,
  AssessmentUpdateRequest,
} from "../types/assessment-type";

const BASE_URL = "/assessments";

export interface AssessmentListParams {
  page: number;
  size: number;
  sort: string;
}

export const getMetrics = async () => {
  const { data } = await axiosInstance.get<ApiResponse<AssessmentMetricDTO[]>>(
    `${BASE_URL}/metrics`,
  );
  return data;
};

export const getAssessments = async (params: AssessmentListParams) => {
  const { data } = await axiosInstance.get<
    PagedApiResponse<AssessmentResponse[]>
  >(BASE_URL, { params });
  return data;
};

export const createAssessment = async (payload: AssessmentCreateRequest) => {
  const { data } = await axiosInstance.post<ApiResponse<AssessmentResponse>>(
    BASE_URL,
    payload,
  );
  return data;
};

export const updateAssessment = async (
  id: number,
  payload: AssessmentUpdateRequest,
) => {
  const { data } = await axiosInstance.put<ApiResponse<AssessmentResponse>>(
    `${BASE_URL}/${id}`,
    payload,
  );
  return data;
};

export const decideAssessment = async (
  id: number,
  payload: AssessmentDecisionRequest,
) => {
  const { data } = await axiosInstance.put<ApiResponse<AssessmentResponse>>(
    `${BASE_URL}/${id}/decision`,
    payload,
  );
  return data;
};

export const deleteAssessment = async (id: number) => {
  const { data } = await axiosInstance.delete<ApiResponse<null>>(
    `${BASE_URL}/${id}`,
  );
  return data;
};
