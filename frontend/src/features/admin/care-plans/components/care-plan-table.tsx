import { useState } from "react";
import { Link } from "react-router";
import { ClipboardList } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import type { CarePlanListItemV2 } from "@/services/care-plan/care-plan-v2-types";
import { CarePlanStatusBadge } from "./care-plan-status-badge";
import { formatCarePlanDate, versionLabel } from "../utils/care-plan-v2-format";
import CarePlanSummaryDialog from "./care-plan-summary-dialog";

export default function CarePlanTable({ carePlans }: { carePlans: CarePlanListItemV2[] }) {
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const selected = carePlans.find((plan) => plan.id === selectedId) ?? null;
  return (
    <>
      <Table>
        <caption className="sr-only">Care plans and their latest versions. Content counts belong to the latest version.</caption>
        <TableHeader className="bg-slate-50">
          <TableRow>
            {["Resident / Admission", "Plan state", "Latest version", "Effective version", "Latest content", "Updated / Created by", ""].map((label, index) => (
              <TableHead key={label || index} className="px-5 py-4 text-xs font-semibold text-slate-500">{label || <span className="sr-only">Actions</span>}</TableHead>
            ))}
          </TableRow>
        </TableHeader>
        <TableBody>
          {carePlans.length === 0 ? <TableRow><TableCell colSpan={7} className="h-56 text-center">
            <ClipboardList className="mx-auto mb-3 size-8 text-slate-300" />
            <p className="font-medium">No care plans found</p>
            <p className="mt-1 text-sm text-muted-foreground">Try changing your filters or create a new care plan.</p>
          </TableCell></TableRow> : carePlans.map((plan) => (
            <TableRow key={plan.id}>
              <TableCell className="min-w-56 px-5 py-5">
                <p className="font-semibold">{plan.residentName?.trim() || `Resident #${plan.residentId}`}</p>
                <p className="mt-1 text-xs text-muted-foreground">Plan #{plan.id} · Admission #{plan.admissionId}</p>
                <p className="mt-1 text-xs text-muted-foreground">{plan.facilityName || "Facility unavailable"}</p>
              </TableCell>
              <TableCell className="px-5"><CarePlanStatusBadge value={plan.state} /></TableCell>
              <TableCell className="min-w-36 px-5">
                <p className="mb-2 text-xs text-muted-foreground">{versionLabel(plan.latestVersionId, plan.latestVersionNumber)}</p>
                {plan.latestVersionId ? <CarePlanStatusBadge value={plan.latestVersionStatus} version /> : <span className="text-sm text-muted-foreground">—</span>}
              </TableCell>
              <TableCell className="px-5 text-sm">
                {plan.effectiveVersionId ? <span className="font-medium text-emerald-700">{versionLabel(plan.effectiveVersionId, plan.effectiveVersionNumber)}</span> : <span className="text-muted-foreground">None</span>}
              </TableCell>
              <TableCell className="px-5 text-xs whitespace-nowrap">
                <p>{plan.goalCount} goals</p><p className="mt-1 text-muted-foreground">{plan.interventionCount} interventions</p>
              </TableCell>
              <TableCell className="px-5 text-xs whitespace-nowrap">
                <p>{formatCarePlanDate(plan.updatedAt)}</p><p className="mt-1 text-muted-foreground">{plan.createdByName?.trim() || "—"}</p>
              </TableCell>
              <TableCell className="px-5"><div className="flex items-center gap-3"><Button variant="outline" aria-label={`View summary for care plan ${plan.id}`} onClick={() => setSelectedId(plan.id)}>Summary</Button><Link className="text-sm font-medium text-primary hover:underline" to={`/admin/care-plans/${plan.id}/edit`}>{plan.latestVersionStatus === "DRAFT" && plan.state === "OPEN" ? "Edit draft" : "View content"}</Link></div></TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
      <CarePlanSummaryDialog plan={selected} onClose={() => setSelectedId(null)} />
    </>
  );
}
