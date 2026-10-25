import { StatusBadge } from "./ul-status-badge";

interface UserTableRowProps {
  user: any;
  onEdit: (user: any) => void;
}

export function UserTableRow({
  user,
  onEdit,
}: UserTableRowProps) {
  return (
    <tr className="border-b border-gray-100 hover:bg-gray-50">

      <td className="px-6 py-5 font-semibold">
        {user.fullName}
      </td>

      <td className="px-6 py-5 text-gray-500">
        {user.email}
      </td>

      <td className="px-6 py-5 text-gray-500">
        {user.phone}
      </td>

      <td className="px-6 py-5">
        {user.role}
      </td>

      <td className="px-6 py-5">
        <StatusBadge
          status={user.status}
        />
      </td>

      <td className="px-6 py-5">
        <StatusBadge
          status={user.twoFA}
          type="2fa"
        />
      </td>

      <td className="px-6 py-5 whitespace-nowrap text-gray-500">
        {user.lastLogin}
      </td>

      <td className="px-6 py-5 text-right">
        <button
          onClick={() => onEdit(user)}
          className="font-semibold text-blue-600 hover:text-blue-800"
        >
          Edit
        </button>
      </td>

    </tr>
  );
}