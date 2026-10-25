export type UserStatus = "INVITED" | "ACTIVE" | "SUSPENDED" | "DEACTIVATED";

export interface UserApiResponse {
  id: number;
  fullName: string;
  email: string;
  phoneNumber: string | null;
  roleName: string;
  status: UserStatus;
  mfaEnabled: boolean;
  lastLoginAt: string | null;
}

export interface UserDetailApiResponse {
  id: number;
  firstName: string;
  middleName: string | null;
  lastName: string;
  email: string;
  phoneNumber: string | null;
  roleId: number;
  roleName: string;
  status: UserStatus;
  facilityId: number | null;
  facilityName: string | null;
}

export interface CreateUserPayload {
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  roleId: number;
  facilityId?: number;
}

export type UpdateUserPayload = CreateUserPayload;

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}