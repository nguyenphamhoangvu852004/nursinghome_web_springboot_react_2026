import { useState, useEffect } from "react";
import { userService } from "@/services/user-service";
import type { UserApiResponse, PageResponse } from "@/types/user";

interface UseUsersParams {
  keyword?: string;
  roleId?: number;
  status?: string;
  page?: number;
  size?: number;
}

export const useUsers = (params: UseUsersParams = {}) => {
  const [data, setData] = useState<PageResponse<UserApiResponse> | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);

        const response = await userService.getUsers({
          keyword: params.keyword,
          roleId: params.roleId,
          status: params.status,
          page: params.page ?? 0,
          size: params.size ?? 10,
        });

        setData(response);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Lỗi tải dữ liệu");
        setData(null);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [
    params.keyword,
    params.roleId,
    params.status,
    params.page,
    params.size,
  ]);

  return {
    data,
    loading,
    error,
  };
};