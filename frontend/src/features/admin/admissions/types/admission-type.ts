export type AdmissionStatus = "ACTIVE" | "DISCHARGED";

export interface AdmissionCreateRequest {
  preAdmissionScreeningId: number;
  facilityId: number;
  bedId: number;
  admissionDate: string;
}

export interface AdmissionDischargeRequest {
  dischargeDate: string;
  dischargeReason: string;
}

export interface AdmissionResponse {
  id: number;
  admissionDate: string;
  residentId: number;
  residentName: string;
  facilityId: number;
  preAdmissionScreeningId: number;
  dischargeDate: string | null;
  dischargeReason: string | null;
  status: AdmissionStatus;
}

export interface AdmissionSelectDTO {
  id: number;
  residentName: string;
}
