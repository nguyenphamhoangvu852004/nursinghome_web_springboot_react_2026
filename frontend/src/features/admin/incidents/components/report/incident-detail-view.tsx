"use client";

import * as React from "react";
import { ChevronRight, Paperclip } from "lucide-react";
import { Button } from "@/components/ui/button";

function Badge({ children, className }: { children: React.ReactNode; className?: string }) {
  return (
    <span className={"inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold " + className}>
      {children}
    </span>
  );
}

function DetailRow({ label, value }: { label: string; value?: React.ReactNode }) {
  return (
    <div className="grid grid-cols-[140px_1fr] items-start gap-4 py-2.5">
      <div className="text-sm font-semibold text-slate-900">{label}</div>
      <div className="text-sm text-slate-600">
        {value || <span className="text-slate-400">—</span>}
      </div>
    </div>
  );
}

export type TimelineEntry = {
  title: string;
  actor: string;
  time: string;
  filled?: boolean;
};

export type IncidentAttachment = { name: string };

// Full shape the detail page can render. Only resident/incidentType/severity/status/reported
// are required — everything else falls back gracefully when the table doesn't have it yet.
export type IncidentDetailsData = {
  id: number | string;
  resident: string;
  room?: string;
  incidentType: string;
  severity: string;
  status: string;
  reported: string;
  reportedBy?: string;
  location?: string;
  description?: string;
  witnesses?: string;
  immediateAction?: string;
  chartStatus?: "Locked" | "Unlocked";
  chartSince?: string;
  sla?: string;
  slaDeadline?: string;
  slaDeadlineHours?: number;
  slaRule?: string;
  attachments?: IncidentAttachment[];
  timeline?: TimelineEntry[];
};

const severityStyles: Record<string, string> = {
  Critical: "bg-red-100 text-red-600",
  Major: "bg-orange-100 text-orange-600",
  Moderate: "bg-amber-100 text-amber-700",
  Minor: "bg-slate-100 text-slate-600",
};

const statusStyles: Record<string, string> = {
  Open: "bg-amber-100 text-amber-700",
  Submitted: "bg-sky-100 text-sky-700",
  Resolved: "bg-emerald-100 text-emerald-700",
};

export function IncidentDetailPage({
  mode = "view",
  data,
}: {
  mode?: "view" | "edit";
  data: IncidentDetailsData;
}) {
  const isLocked = data.chartStatus !== "Unlocked";
  const isOverdue = (data.sla ?? "").toUpperCase().includes("OVERDUE");
  const hasSla = !!data.sla && data.sla !== "—";
  const slaText = isOverdue ? "Overdue" : (data.sla ?? "").replace("left", "remaining");

  const timeline: TimelineEntry[] =
    data.timeline && data.timeline.length > 0
      ? data.timeline
      : [
          {
            title: "Incident reported",
            actor: data.reportedBy ?? "Staff",
            time: data.reported,
            filled: true,
          },
        ];

  return (
    <div className="space-y-4">
      {/* Breadcrumb */}
      <div className="flex items-center gap-1.5 text-sm text-slate-500">
        <span>Incident &amp; Risk</span>
        <ChevronRight className="size-3.5" />
        <span>Incident List</span>
        <ChevronRight className="size-3.5" />
        <span>#{data.id}</span>
      </div>

      {/* Title */}
      <div>
        <div className="flex flex-wrap items-center gap-3">
          <h1 className="text-2xl font-bold text-slate-900">
            Incident #{data.id} — {data.resident}
          </h1>
          <Badge className={statusStyles[data.status] ?? "bg-slate-100 text-slate-600"}>
            {data.status}
          </Badge>
          <Badge className={severityStyles[data.severity] ?? "bg-slate-100 text-slate-600"}>
            {data.severity}
          </Badge>
        </div>
        <p className="mt-1 text-sm text-slate-500">
          {data.incidentType}
          {data.room ? ` · Room ${data.room}` : ""}
          {data.reportedBy ? ` · Reported by ${data.reportedBy}` : ""} · {data.reported}
        </p>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1fr_320px]">
        {/* Main column */}
        <div className="space-y-6">
          <div className="rounded-[20px] border border-gray-200 bg-white p-6 shadow-sm">
            <h2 className="mb-2 text-base font-semibold text-slate-900">Report Details (read-only)</h2>
            <div className="divide-y divide-gray-100">
              <DetailRow label="Location" value={data.location} />
              <DetailRow label="Description" value={data.description} />
              <DetailRow label="Witnesses" value={data.witnesses} />
              <DetailRow label="Immediate action" value={data.immediateAction} />
            </div>
          </div>

          <div>
            <h2 className="mb-3 text-base font-semibold text-slate-900">Attachments (simulated)</h2>
            {data.attachments && data.attachments.length > 0 ? (
              <div className="flex flex-wrap gap-3">
                {data.attachments.map((file) => (
                  <div
                    key={file.name}
                    className="inline-flex items-center gap-2 rounded-md border border-gray-200 bg-white px-3 py-2 text-sm text-slate-700 shadow-sm"
                  >
                    <Paperclip className="size-4 text-slate-400" />
                    {file.name}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-sm text-slate-400">No attachments recorded.</p>
            )}
          </div>

          <div>
            <h2 className="mb-4 text-base font-semibold text-slate-900">Timeline</h2>
            <div className="relative space-y-6 pl-6">
              <div className="absolute left-[5px] top-1.5 bottom-1.5 w-px bg-gray-200" />
              {timeline.map((entry, i) => (
                <div key={i} className="relative">
                  <span
                    className={
                      "absolute -left-6 top-1 size-2.5 rounded-full border-2 " +
                      (entry.filled ? "border-blue-600 bg-blue-600" : "border-blue-600 bg-white")
                    }
                  />
                  <p className="text-sm font-semibold text-slate-900">{entry.title}</p>
                  <p className="text-sm text-slate-500">
                    {entry.actor} · {entry.time}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {hasSla && (
            <div
              className={
                "rounded-[20px] border p-5 " +
                (isOverdue ? "border-red-200 bg-red-50" : "border-emerald-200 bg-emerald-50")
              }
            >
              <h3 className={"text-sm font-semibold " + (isOverdue ? "text-red-700" : "text-emerald-700")}>
                SLA — Regulatory Reporting
              </h3>
              <p className={"mt-2 text-3xl font-bold " + (isOverdue ? "text-red-700" : "text-emerald-700")}>
                {slaText}
              </p>
              {data.slaDeadline && (
                <p className={"mt-3 text-sm " + (isOverdue ? "text-red-700" : "text-emerald-700")}>
                  Report by {data.slaDeadline}
                </p>
              )}
              {data.slaRule && (
                <p className={"text-sm " + (isOverdue ? "text-red-700" : "text-emerald-700")}>{data.slaRule}</p>
              )}
            </div>
          )}

          <div className="rounded-[20px] border border-gray-200 bg-white p-5 shadow-sm">
            <h3 className="mb-3 text-base font-semibold text-slate-900">Chart Status</h3>
            <div className="flex items-center gap-3">
              <Badge className={isLocked ? "bg-red-100 text-red-600" : "bg-emerald-100 text-emerald-700"}>
                {isLocked ? "Locked" : "Unlocked"}
              </Badge>
              {data.chartSince && <span className="text-sm text-slate-500">since {data.chartSince}</span>}
            </div>
          </div>

          <div className="rounded-[20px] border border-gray-200 bg-white p-5 shadow-sm">
            <h3 className="mb-3 text-base font-semibold text-slate-900">DON Actions</h3>
            <div className="space-y-2.5">
              <Button variant="outline" className="w-full justify-center">
                Add Progress Note
              </Button>
              <Button variant="outline" className="w-full justify-center">
                Submit External Report
              </Button>
              {isLocked ? (
                <>
                  <Button
                    variant="outline"
                    className="w-full justify-center border-red-300 text-red-600 hover:bg-red-50 hover:text-red-700"
                  >
                    Unlock Chart
                  </Button>
                  <Button variant="outline" disabled className="w-full justify-center text-slate-400">
                    Mark Resolved
                  </Button>
                  <p className="pt-1 text-xs text-slate-400">Unlock chart to enable (UC-M7-10).</p>
                </>
              ) : (
                <Button variant="outline" className="w-full justify-center">
                  Mark Resolved
                </Button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}