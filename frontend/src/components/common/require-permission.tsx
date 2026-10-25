import type { PermissionCode } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";
import { Navigate } from "react-router";

interface RequirePermissionProps {
  permission: PermissionCode | PermissionCode[];
  children: React.ReactNode;
}

export const RequirePermission = ({
  permission,
  children,
}: RequirePermissionProps) => {
  const { canAccess, isLoading } = usePermissions();

  if (isLoading) return null;
  if (!canAccess(permission)) return <Navigate to={"/admin/403"} replace />;

  return <>{children}</>;
};
