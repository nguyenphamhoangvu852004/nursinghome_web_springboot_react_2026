import apiClient from "@/common/config/api";
import type {
  CreateUserPayload,
  UpdateUserPayload,
  UserStatus,
} from "@/types/user";

const BASE = "/admin/users";

interface GetUsersParams {
  keyword?: string;
  roleId?: number | null;
  status?: string;
  page?: number;
  size?: number;
}

export const userService = {
  getUsers: async (params: GetUsersParams = {}) => {
    const queryParams: Record<string, unknown> = {};

    if (params.keyword?.trim()) {
      queryParams.keyword = params.keyword.trim();
    }

    if (params.roleId != null) {
      queryParams.roleId = params.roleId;
    }

    if (params.status?.trim()) {
      queryParams.status = params.status.trim();
    }

    if (params.page !== undefined) {
      queryParams.page = params.page;
    }

    if (params.size !== undefined) {
      queryParams.size = params.size;
    }

    const response = await apiClient.get(BASE, {
      params: queryParams,
    });

    return response.data;
  },

  getUserById: async (id: number) => {
    const response = await apiClient.get(`${BASE}/${id}`);
    return response.data;
  },

  createUser: async (payload: CreateUserPayload) => {
    const response = await apiClient.post(BASE, payload);
    return response.data;
  },

  updateUser: async (id: number, payload: UpdateUserPayload) => {
    const response = await apiClient.put(`${BASE}/${id}`, payload);
    return response.data;
  },

  changeUserStatus: async (id: number, status: UserStatus) => {
    const response = await apiClient.patch(`${BASE}/${id}/status`, {
      status,
    });

    return response.data;
  },

  getActiveCnas: async () => {
    const response = await apiClient.get("/users/cnas");
    return response.data;
  },
};