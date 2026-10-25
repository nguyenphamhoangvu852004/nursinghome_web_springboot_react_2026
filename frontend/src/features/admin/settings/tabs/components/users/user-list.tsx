import { useState } from "react";
import {
  Users,
  UserCheck,
  UserPlus,
  UserX,
} from "lucide-react";

import { useUsers } from "@/hooks/use-users";
import type { UserApiResponse } from "@/types/user";

import { StatisticCard } from "./ul-statistic-card";
import { SearchFilter } from "./ul-search-filter";
import { UserTable } from "./ul-table";
import { Pagination } from "./ul-pagination";

interface UserListProps {
  onAdd: () => void;
  onEdit: (user: any) => void;
}

export function UserList({
  onAdd,
  onEdit,
}: UserListProps) {

  const [search, setSearch] = useState("");
  const [role, setRole] = useState("ALL");
  const [status, setStatus] = useState("ALL");

  const [page, setPage] = useState(1);
  const pageSize = 10;

  const { data, loading, error } = useUsers({
    keyword: search || undefined,
    roleId: role !== "ALL" ? Number(role) : undefined,
    status: status !== "ALL" ? status : undefined,
    page: page - 1,
    size: pageSize,
  });

  const transformedUsers = (data?.content || []).map(
    (user: UserApiResponse) => ({
      id: user.id,
      fullName: user.fullName,
      email: user.email,
      phone: user.phoneNumber || "--",
      role: user.roleName,
      status: user.status,
      twoFA: user.mfaEnabled ? "Enabled" : "Not-set",
      lastLogin: user.lastLoginAt || "--",
    })
  );

  const totalUsers = data?.totalElements || 0;

  const activeCount = transformedUsers.filter(
    (u) => u.status === "ACTIVE"
  ).length;

  const invitedCount = transformedUsers.filter(
    (u) => u.status === "INVITED"
  ).length;

  const suspendedDeactivatedCount = transformedUsers.filter(
    (u) =>
      u.status === "SUSPENDED" ||
      u.status === "DEACTIVATED"
  ).length;

  return (
    <div className="space-y-6">

      <div>
        <p className="text-sm text-gray-400">
          Admin &gt; Users
        </p>

        <h1 className="mt-1 text-3xl font-bold text-gray-800">
          User List
        </h1>

        <p className="text-gray-500">
          All user accounts
        </p>
      </div>

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">

        <StatisticCard
          label="Total Users"
          value={totalUsers}
          icon={<Users size={20} />}
          iconBgClassName="bg-blue-100"
        />

        <StatisticCard
          label="Active"
          value={activeCount}
          icon={<UserCheck size={20} />}
          iconBgClassName="bg-green-100"
        />

        <StatisticCard
          label="Invited"
          value={invitedCount}
          icon={<UserPlus size={20} />}
          iconBgClassName="bg-yellow-100"
        />

        <StatisticCard
          label="Suspended / Deactivated"
          value={suspendedDeactivatedCount}
          icon={<UserX size={20} />}
          iconBgClassName="bg-red-100"
        />

      </div>

      <SearchFilter
        search={search}
        role={role}
        status={status}
        onSearchChange={(value) => {
          setSearch(value);
          setPage(1);
        }}
        onRoleChange={(value) => {
          setRole(value);
          setPage(1);
        }}
        onStatusChange={(value) => {
          setStatus(value);
          setPage(1);
        }}
        onReset={() => {
          setSearch("");
          setRole("ALL");
          setStatus("ALL");
          setPage(1);
        }}
        onAdd={onAdd}
      />

      {error && (
        <div className="rounded-lg bg-red-50 p-4 text-red-600">
          {error}
        </div>
      )}

      <UserTable
        users={transformedUsers}
        isLoading={loading}
        onEdit={onEdit}
      />

      <Pagination
        page={page}
        totalPages={data?.totalPages || 1}
        total={data?.totalElements || 0}
        pageSize={pageSize}
        onPageChange={setPage}
      />

    </div>
  );
}