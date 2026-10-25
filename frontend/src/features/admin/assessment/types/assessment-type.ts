export interface AssessmentMetricDTO {
  id: number;
  category: "ADL" | "IADL" | "BRADEN" | "MORSE";
  metricName: string;
}

export interface AssessmentDetailRequest {
  metricId: number;
  score: number;
  notes?: string;
}

export interface AssessmentDetailResponse {
  metricId: number;
  metricName: string;
  category: string;
  score: number;
  notes?: string;
}

export interface AssessmentResponse {
  id: number;
  status: "DRAFT" | "COMPLETED" | "REJECTED";
  adlTotalScore: number;
  residentId: number;
  suggestedCareLevelId: number;
  residentName: string;
  isOverridden: boolean | null;
  overrideReason?: string | null;
  admissionId: number;
  details: AssessmentDetailResponse[];
}

export interface AssessmentDecisionRequest {
  status: "COMPLETED" | "REJECTED";
  confirmedCareLevelId: number;
  overrideReason?: string;
}

export interface AssessmentCreateRequest {
  admissionId: number;
  details: AssessmentDetailRequest[];
}

export interface AssessmentUpdateRequest {
  details: AssessmentDetailRequest[];
}

export interface AssessmentDecisionRequest {
  status: "COMPLETED" | "REJECTED";
  confirmedCareLevelId: number;
  overrideReason?: string;
}
