import { carePlanStates, carePlanVersionStatuses } from "@/services/care-plan/care-plan-v2-types";

export function CarePlanStatusBadge({ value, version = false }: { value: string | null; version?: boolean }) {
  const labels: Record<string, string> = version ? carePlanVersionStatuses : carePlanStates;
  const color = value === "OPEN" || value === "EFFECTIVE" ? "bg-emerald-50 text-emerald-700 ring-emerald-600/15"
    : value === "PENDING_APPROVAL" ? "bg-amber-50 text-amber-700 ring-amber-600/15"
      : "bg-slate-100 text-slate-600 ring-slate-500/15";
  return <span className={`inline-flex whitespace-nowrap rounded-full px-2.5 py-1 text-xs font-medium ring-1 ring-inset ${color}`}>{value ? labels[value] ?? value : "Not available"}</span>;
}

