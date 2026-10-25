export type ScreeningStatus = "DRAFT" | "COMPLETED" | "REJECTED";

export interface PreResponse {
  id: number;
  status: ScreeningStatus;
  residentId: number;
  residentName: string;
  createdAt: string;
}

export interface PreCreateRequest {
  residentId: number;
}

export interface PreDecisionRequest {
  status: "COMPLETED" | "REJECTED";
}

export interface PreSelectDTO {
  id: number;
  residentName: string;
}
