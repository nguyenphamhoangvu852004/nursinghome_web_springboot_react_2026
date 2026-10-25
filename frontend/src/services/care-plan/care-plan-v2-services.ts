import { CARE_PLAN_API_BASE_URL } from "@/common/config/api-url";
import { apiClient } from "@/lib/api-client";
import type { ApiResponse, PagedApiResponse } from "@/types/api";
import type { AdmissionResponse, AdmissionSelectDTO } from "@/features/admin/admissions/types/admission-type";
import type { CarePlanListParamsV2, CarePlanListResponseV2 } from "./care-plan-v2-types";

export async function getCarePlansV2(params: CarePlanListParamsV2, signal?: AbortSignal) {
  const { data } = await apiClient.get<CarePlanListResponseV2>("/care-plans", {
    params, signal, baseURL: CARE_PLAN_API_BASE_URL,
  });
  if (data.statusCode >= 400) throw new Error(data.message);
  return data;
}

export interface CreateCarePlanV2Request {
  residentId: number;
  admissionId: number;
}

export interface CreateCarePlanV2Response {
  id: number;
}

export async function getCarePlanAdmissions(signal?: AbortSignal) {
  const { data: active } = await apiClient.get<ApiResponse<AdmissionSelectDTO[]>>(
    "/admissions/select-active", { signal },
  );
  if (active.statusCode >= 400) throw new Error(active.message);
  const activeIds = new Set(active.data.map((admission) => admission.id));
  const admissions: AdmissionResponse[] = [];
  let page = 0;
  let hasNext = true;
  while (hasNext) {
    const { data } = await apiClient.get<PagedApiResponse<AdmissionResponse[]>>(
      "/admissions", { params: { page, size: 100, sort: "admissionDate,desc" }, signal },
    );
    if (data.statusCode >= 400) throw new Error(data.message);
    admissions.push(...data.data);
    hasNext = data.metadata.hasNext;
    page += 1;
  }
  const now = new Date();
  const today = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}-${String(now.getDate()).padStart(2, "0")}`;
  return admissions.filter((admission) =>
    activeIds.has(admission.id) && admission.status === "ACTIVE" && !admission.dischargeDate &&
    admission.admissionDate.slice(0, 10) <= today,
  );
}

export async function createCarePlanV2(payload: CreateCarePlanV2Request) {
  // Use the shared auth/error interceptors with the shared API namespace.
  const { data } = await apiClient.post<ApiResponse<CreateCarePlanV2Response>>(
    "/care-plans", payload, { baseURL: CARE_PLAN_API_BASE_URL },
  );
  if (data.statusCode >= 400) throw new Error(data.message);
  if (!Number.isSafeInteger(data.data?.id) || data.data.id <= 0) {
    throw new Error("The server did not return a care plan ID. Check the admission before creating another plan.");
  }
  return data.data;
}
