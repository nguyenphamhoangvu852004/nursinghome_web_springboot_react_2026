// LOC Rate Types
export interface CareLevelRateResponse {
  id: number;
  care_level_id: number;
  facility_id: number;
  daily_rate: number;
  effective_from: string;
  effective_to: string | null;
}

export interface CreateCareLevelRateRequest {
  care_level_id: number;
  facility_id: number;
  daily_rate: number;
  effective_from: string;
}

export interface UpdateCareLevelRateRequest {
  care_level_id?: number;
  facility_id?: number;
  daily_rate?: number;
  effective_from?: string;
  effective_to?: string | null;
}

export interface CareLevelResponse {
  id: number;
  level_code: string;
  level_name: string;
  is_deleted: boolean;
}

export interface UpdateCareLevelRequest {
  is_deleted: boolean;
}
