import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog";
import type { CarePlanListItemV2 } from "@/services/care-plan/care-plan-v2-types";
import { CarePlanStatusBadge } from "./care-plan-status-badge";
import { formatCarePlanDate, versionLabel } from "../utils/care-plan-v2-format";

export default function CarePlanSummaryDialog({ plan, onClose }: { plan: CarePlanListItemV2 | null; onClose: () => void }) {
  return (
    <Dialog open={plan !== null} onOpenChange={(open) => { if (!open) onClose(); }}>
      <DialogContent className="max-h-[85vh] overflow-y-auto p-6 sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Care plan #{plan?.id}</DialogTitle>
          <DialogDescription>Plan and latest version summary</DialogDescription>
        </DialogHeader>
        {plan && <>
          <div className="flex flex-wrap gap-2"><CarePlanStatusBadge value={plan.state} /><CarePlanStatusBadge value={plan.latestVersionStatus} version /></div>
          <dl className="grid gap-5 py-3 text-sm sm:grid-cols-2">
            {[
              ["Resident", plan.residentName?.trim() || `Resident #${plan.residentId}`],
              ["Resident ID", `#${plan.residentId}`],
              ["Admission", `#${plan.admissionId} · ${formatCarePlanDate(plan.admissionDate)}`],
              ["Facility", plan.facilityName || "—"],
              ["Current room / bed", [plan.roomNumber, plan.bedNumber].filter(Boolean).join(" / ") || "Unassigned"],
              ["Created by", plan.createdByName?.trim() || "—"],
              ["Latest version", versionLabel(plan.latestVersionId, plan.latestVersionNumber)],
              ["Current effective version", plan.effectiveVersionId ? versionLabel(plan.effectiveVersionId, plan.effectiveVersionNumber) : "No effective version"],
              ["Goals in latest version", plan.goalCount.toLocaleString()],
              ["Interventions in latest version", plan.interventionCount.toLocaleString()],
              ["Opened", formatCarePlanDate(plan.openedAt, true)],
              ["Created", formatCarePlanDate(plan.createdAt, true)],
              ["Last updated", formatCarePlanDate(plan.updatedAt, true)],
              ["Closed", formatCarePlanDate(plan.closedAt, true)],
            ].map(([label, value]) => <div key={label}><dt className="text-muted-foreground">{label}</dt><dd className="mt-1 break-words font-medium">{value}</dd></div>)}
          </dl>
          {plan.closedReason && <div className="rounded-lg bg-slate-50 p-4 text-sm"><p className="font-medium">Closure reason</p><p className="mt-1 whitespace-pre-wrap text-muted-foreground">{plan.closedReason}</p></div>}
          {plan.state === "OPEN" && !plan.effectiveVersionId && <p className="rounded-lg bg-blue-50 p-4 text-sm text-blue-900">This plan has no effective version yet. A draft must complete review and approval before it can be applied.</p>}
        </>}
      </DialogContent>
    </Dialog>
  );
}
