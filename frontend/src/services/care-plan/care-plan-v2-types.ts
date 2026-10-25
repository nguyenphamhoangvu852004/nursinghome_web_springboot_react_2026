import type { PagedApiResponse } from "@/types/api";

export const carePlanStates = {
  OPEN: "Open", CLOSED_DISCHARGED: "Closed · Discharged", CLOSED_TRANSFERRED: "Closed · Transferred",
  CLOSED_DECEASED: "Closed · Deceased", VOIDED: "Voided",
} as const;
export const carePlanVersionStatuses = {
  DRAFT: "Draft", PENDING_APPROVAL: "Pending review", EFFECTIVE: "Effective", SUPERSEDED: "Superseded",
} as const;
export type CarePlanState = keyof typeof carePlanStates;
export type CarePlanVersionStatus = keyof typeof carePlanVersionStatuses;

export interface CarePlanListItemV2 {
  id: number;
  residentId: number;
  residentName: string | null;
  roomNumber: string | null;
  bedNumber: string | null;
  admissionId: number;
  admissionDate: string | null;
  facilityName: string | null;
  state: CarePlanState | null;
  latestVersionId: number | null;
  latestVersionNumber: number | null;
  latestVersionStatus: CarePlanVersionStatus | null;
  effectiveVersionId: number | null;
  effectiveVersionNumber: number | null;
  goalCount: number;
  interventionCount: number;
  createdByName: string | null;
  openedAt: string | null;
  closedAt: string | null;
  closedReason: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}
export interface CarePlanStatisticsV2 {
  total: number;
  open: number;
  draft: number;
  pendingApproval: number;
  effective: number;
}
export interface CarePlanListParamsV2 {
  page: number;
  size: number;
  search?: string;
  state?: CarePlanState;
  versionStatus?: CarePlanVersionStatus;
  sortBy?: "updatedAt" | "createdAt" | "residentName" | "id";
  sortDir?: "ASC" | "DESC";
}
export type CarePlanListResponseV2 = PagedApiResponse<{
  list: CarePlanListItemV2[];
  statistics: CarePlanStatisticsV2;
}>;
