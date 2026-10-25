import { UserTableRow } from "./ul-table-row";

interface UserTableProps {
  users: any[];
  isLoading: boolean;
  onEdit: (user: any) => void;
}

const COLUMNS = [
  "Name",
  "Email",
  "Phone",
  "Role",
  "Status",
  "2FA",
  "Last Login",
  "",
];

export function UserTable({
  users,
  isLoading,
  onEdit,
}: UserTableProps) {
  return (
    <div className="overflow-x-auto rounded-xl border border-gray-200 bg-white">
      <table className="min-w-full">
        <thead className="bg-gray-50">
          <tr>
            {COLUMNS.map((column) => (
              <th
                key={column}
                className="border-b border-gray-200 px-6 py-4 text-left text-sm font-semibold text-gray-600"
              >
                {column}
              </th>
            ))}
          </tr>
        </thead>

        <tbody>
          {isLoading ? (
            <tr>
              <td
                colSpan={8}
                className="py-10 text-center text-gray-400"
              >
                Loading...
              </td>
            </tr>
          ) : (
            users.map((user) => (
              <UserTableRow
                key={user.id}
                user={user}
                onEdit={onEdit}
              />
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}