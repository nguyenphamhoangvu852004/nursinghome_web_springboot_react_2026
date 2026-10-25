interface RoleBadgeProps {
  role: string;
}

export function RoleBadge({ role }: RoleBadgeProps) {
  let color = "bg-gray-100 text-gray-700";

  switch (role) {
    case "System Admin":
      color = "bg-purple-100 text-purple-700";
      break;

    case "Admin":
      color = "bg-blue-100 text-blue-700";
      break;

    case "Doctor":
      color = "bg-green-100 text-green-700";
      break;

    case "Nurse":
      color = "bg-cyan-100 text-cyan-700";
      break;

    case "Billing":
      color = "bg-orange-100 text-orange-700";
      break;

    case "CNA":
      color = "bg-pink-100 text-pink-700";
      break;
  }

  return (
    <span
      className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-medium ${color}`}
    >
      {role}
    </span>
  );
}