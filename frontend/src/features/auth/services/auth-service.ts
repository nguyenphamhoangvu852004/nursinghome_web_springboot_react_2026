import { axiosInstance as axios } from "@/lib/axios";
import {
  mockUsersByRole,
  CURRENT_MOCK_ROLE,
  type MockUser,
} from "@/mocks/permission-data";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  sessionId: string;
  role: string;
  name: string;
}

export interface ResetPasswordRequest {
  email: string;
}

export const fetchCurrentUser = async (): Promise<MockUser> => {
  return mockUsersByRole[CURRENT_MOCK_ROLE];
};

export const authService = {
  Login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await axios.post("/auth/login", data);
    return response.data;
  },

  ResetPassword: async (data: ResetPasswordRequest): Promise<void> => {
    await axios.post("/auth/reset-password", data);
  },
};
