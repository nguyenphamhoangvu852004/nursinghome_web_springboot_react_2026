import apiClient from "@/common/config/api";
import type { Role } from "../types/role.type";

export const getRoleList = async (): Promise<Role[]> => {
    const response = await apiClient.get<Role[]>("/admin/roles");

    return response.data;
};
