import { ClipboardList, FilePenLine, Clock3, HeartPulse, FolderOpen } from "lucide-react";
import type { CarePlanStatisticsV2 } from "@/services/care-plan/care-plan-v2-types";

export default function CarePlanStatistical({ statistics }: { statistics?: CarePlanStatisticsV2 }) {
  const cards = [
    { label: "Matching plans", value: statistics?.total, Icon: ClipboardList, color: "bg-blue-50 text-blue-600" },
    { label: "Open plans", value: statistics?.open, Icon: FolderOpen, color: "bg-teal-50 text-teal-700" },
    { label: "Open · Latest draft", value: statistics?.draft, Icon: FilePenLine, color: "bg-slate-100 text-slate-600" },
    { label: "Open · Pending review", value: statistics?.pendingApproval, Icon: Clock3, color: "bg-amber-50 text-amber-700" },
    { label: "Open · Effective plan", value: statistics?.effective, Icon: HeartPulse, color: "bg-emerald-50 text-emerald-700" },
  ];
  return (
    <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-5">
      {cards.map(({ label, value, Icon, color }) => (
        <div key={label} className="rounded-xl border bg-white p-5 shadow-sm">
          <div className="flex items-center justify-between gap-3">
            <p className="text-xs font-medium text-muted-foreground">{label}</p>
            <span className={`rounded-lg p-2 ${color}`}><Icon className="size-4" /></span>
          </div>
          <p className="mt-3 text-3xl font-semibold tabular-nums">{value?.toLocaleString() ?? "—"}</p>
        </div>
      ))}
    </div>
  );
}
