import { useQuery } from "@tanstack/react-query";
import { fetchCurrentUser } from "../services/auth-service";
import type { PermissionCode } from "@/common/permissions";

export const useCurrentUser = () => {
  return useQuery({
    queryKey: ["currentUser"],
    queryFn: fetchCurrentUser,
  });
};

export const usePermissions = () => {
  const { data, isLoading } = useCurrentUser();
  const permissions = data?.permissions ?? [];
  const can = (permission: PermissionCode) => permissions.includes(permission);

  const canAny = (permissionCodes: PermissionCode[]) =>
    permissionCodes.some((code) => permissions.includes(code));

  const canAccess = (required: PermissionCode | PermissionCode[]) =>
    Array.isArray(required) ? canAny(required) : can(required);

  return { permissions, canAccess, can, isLoading };
};
