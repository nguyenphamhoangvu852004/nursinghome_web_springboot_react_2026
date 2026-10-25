import { axiosInstance as axios } from "@/lib/axios";

export interface AuditLogParams {
  table_name?: string;
  record_id?: string;
  performed_by?: string;
  action?: string;
  dateFrom?: string;
  dateTo?: string;
  page?: number;
  pageSize?: number;
}

export interface PhiAccessLogParams {
  table_name?: string;
  record_id?: string;
  accessed_by?: string;
  access_type?: string;
  page?: number;
  pageSize?: number;
}

export interface PaginatedResponse<T> {
  data: T[];
  page: {
    page: number;
    pageSize: number;
    totalItems: number;
    totalPages: number;
  };
}

export interface AuditLog {
  id: number;
  table_name: string;
  record_id: string;
  action: string;
  old_data: string;
  new_data: string;
  performed_by: number;
  performed_at: string;
  ip_address: string;
}

export interface PhiAccessLog {
  id: number;
  table_name: string;
  record_id: string;
  accessed_by: number;
  access_type: string;
  access_reason: string;
  ip_address: string;
  accessed_at: string;
}
export interface ActiveSession {
  session_id: string;
  user_id: number;
  login_at: string;
  last_activity_at: string;
  status: string;
}

export const adminService = {
  GetAuditLogs: async (params: AuditLogParams): Promise<PaginatedResponse<AuditLog>> => {
    const response = await axios.get("/admin/security-telemetry/audit-logs", { params });
    return response.data;
  },

  GetPhiAccessLogs: async (params: PhiAccessLogParams): Promise<PaginatedResponse<PhiAccessLog>> => {
    const response = await axios.get("/admin/security-telemetry/phi-access-logs", { params });
    return response.data;
  },

  GetActiveSessions: async (): Promise<ActiveSession[]> => {
    const response = await axios.get("/admin/security-telemetry/sessions");
    return response.data;
  },

  ForceLogout: async (sessionId: string): Promise<void> => {
    await axios.post(`/admin/security-telemetry/sessions/${sessionId}/force-logout`);
  },

  ChangeUserStatus: async (userId: number, status: string): Promise<void> => {
    await axios.patch(`/admin/security-telemetry/users/${userId}/status`, { status });
  },

  GetUsers: async (): Promise<any[]> => {
    const response = await axios.get("/admin/security-telemetry/users");
    return response.data;
  },
};
