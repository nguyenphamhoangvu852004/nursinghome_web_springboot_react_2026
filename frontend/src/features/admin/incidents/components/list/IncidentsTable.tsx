import React, { useState } from "react";
import { X } from "lucide-react";
import {
  IncidentDetailPage,
  type IncidentDetailsData,
} from "@/features/admin/incidents/components/report/incident-detail-view";
import { ChartLockedModal } from "@/features/admin/incidents/components/report/chart-locked-modal";
import { incidentsApi, type IncidentApiResponse } from "@/services/incidents/incidents-api";

type Incident = {
  id: number;
  resident: string;
  type: string;
  severity: string;
  reported: string;
  sla: string;
  slaStyle: string;
  status: string;
  chart: string;
};

const typeLabels: Record<string, string> = {
  FALL: "Fall",
  MEDICATION_ERROR: "Medication Error",
  ALTERCATION: "Altercation",
  SKIN_TEAR: "Skin Tear",
};

const statusLabels: Record<string, string> = {
  OPEN: "Open",
  UNDER_INVESTIGATION: "Under Investigation",
  SUBMITTED: "Submitted",
  RESOLVED: "Resolved",
};

// Converts the full detail response from GET /api/v1/incidents/{id}
// into the shape IncidentDetailPage expects.
function mapFullDetail(api: IncidentApiResponse): IncidentDetailsData {
  const room = api.resident.bed?.room?.roomNumber;

  let sla = "—";
  if (api.slaCountDown !== null && api.slaCountDown !== undefined) {
    sla = api.slaCountDown < 0 ? "OVERDUE" : `${api.slaCountDown}h left`;
  }

  const timeline = (api.timelines ?? [])
    .slice()
    .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1)) // newest first
    .map((t, idx) => ({
      title: t.action,
      actor: t.actor?.displayName ?? "System",
      time: t.createdAt.replace("T", " ").slice(0, 16),
      filled: idx === 0,
    }));

  return {
    id: `INC-${api.id}`,
    resident: api.resident.displayName,
    room,
    incidentType: typeLabels[api.incidentType] ?? api.incidentType,
    severity: api.severity.levelName,
    status: statusLabels[api.status] ?? api.status,
    reported: api.reportedAt ? api.reportedAt.replace("T", " ").slice(0, 16) : "",
    reportedBy: api.reporter?.displayName,
    location: api.location,
    description: api.description,
    witnesses: api.witnesses,
    chartStatus: api.isLocked ? "Locked" : "Unlocked",
    sla,
    slaDeadlineHours: api.slaDeadlineHours,
    timeline: timeline.length > 0 ? timeline : undefined,
  };
}

export function IncidentsTable({
  incidents,
  severityStyles,
  statusStyles,
  chartStyles,
  children,
}: {
  incidents: Incident[];
  severityStyles: Record<string, string>;
  statusStyles: Record<string, string>;
  chartStyles: Record<string, string>;
  children?: React.ReactNode;
}) {
  const [selected, setSelected] = useState<Incident | null>(null);
  const [detail, setDetail] = useState<IncidentDetailsData | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);
  // When a locked incident is opened, we show the Chart Locked notice first.
  // Clicking "View Incident" inside that notice flips this to true and reveals
  // the normal read-only detail view underneath.
  const [showDetailAnyway, setShowDetailAnyway] = useState(false);

  const isLockedRow = (inc: Incident) => inc.chart === "Locked";

  // Fallback shape built straight from the table row, used while the detail
  // call is loading or if it fails.
  const fallbackFrom = (inc: Incident): IncidentDetailsData => {
    const [name, room] = inc.resident.split(" · ");
    return {
      id: `INC-${2000 + inc.id}`,
      resident: name ?? inc.resident,
      room,
      incidentType: inc.type,
      severity: inc.severity,
      status: inc.status,
      reported: inc.reported,
      sla: inc.sla,
      chartStatus: inc.chart as "Locked" | "Unlocked",
    };
  };

  const open = async (incident: Incident) => {
    setSelected(incident);
    setShowDetailAnyway(false);
    setDetail(fallbackFrom(incident));
    setDetailError(null);
    setDetailLoading(true);
    try {
      const full = await incidentsApi.getById(incident.id);
      setDetail(mapFullDetail(full));
    } catch (e: any) {
      setDetailError(e?.response?.data?.message ?? e.message ?? "Không tải được chi tiết incident");
      // keep the fallback data already set above
    } finally {
      setDetailLoading(false);
    }
  };

  const close = () => {
    setSelected(null);
    setDetail(null);
    setDetailError(null);
    setShowDetailAnyway(false);
  };

  return (
    <>
      <div className="overflow-x-auto rounded-[20px] border border-gray-200 bg-white shadow-sm">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-slate-50">
            <tr>
              {[
                "Resident",
                "Type",
                "Severity",
                "Reported",
                "SLA Countdown ▲",
                "Status",
                "Chart",
                "",
              ].map((heading) => (
                <th
                  key={heading}
                  className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-[0.2em] text-slate-500"
                >
                  {heading}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {incidents.map((incident) => (
              <tr key={incident.id} className="hover:bg-slate-50">
                <td className="px-6 py-5 font-semibold text-slate-900">{incident.resident}</td>
                <td className="px-6 py-5 text-slate-600">{incident.type}</td>
                <td className="px-6 py-5">
                  <span
                    className={
                      "inline-flex rounded-full px-3 py-1 text-xs font-semibold " +
                      severityStyles[incident.severity]
                    }
                  >
                    {incident.severity}
                  </span>
                </td>
                <td className="px-6 py-5 text-slate-600">{incident.reported}</td>
                <td className={"px-6 py-5 font-semibold " + incident.slaStyle}>{incident.sla}</td>
                <td className="px-6 py-5">
                  <span
                    className={
                      "inline-flex rounded-full px-3 py-1 text-xs font-semibold " + statusStyles[incident.status]
                    }
                  >
                    {incident.status}
                  </span>
                </td>
                <td className="px-6 py-5">
                  <span
                    className={
                      "inline-flex rounded-full px-3 py-1 text-xs font-semibold " + chartStyles[incident.chart]
                    }
                  >
                    {incident.chart}
                  </span>
                </td>
                <td className="px-6 py-5 text-right">
                  <button
                    onClick={() => open(incident)}
                    className="text-sm font-semibold text-sky-600 hover:text-sky-800"
                  >
                    View
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {children}
      </div>

      {selected && isLockedRow(selected) && !showDetailAnyway && (
        <ChartLockedModal
          open
          residentName={selected.resident.split(" · ")[0] ?? selected.resident}
          lockedAt={detail?.reported ?? selected.reported}
          incidentId={detail?.id ?? `INC-${2000 + selected.id}`}
          onBackToProfile={close}
          onViewIncident={() => setShowDetailAnyway(true)}
        />
      )}

      {selected && (!isLockedRow(selected) || showDetailAnyway) && (
        <div
          className="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-black/50 p-6"
          onClick={close}
        >
          <div
            className="relative mx-auto w-full max-w-4xl rounded-lg bg-white p-6 shadow-lg"
            onClick={(e) => e.stopPropagation()}
          >
            <button
              onClick={close}
              aria-label="Close"
              className="absolute right-4 top-4 text-slate-400 hover:text-slate-600"
            >
              <X className="size-5" />
            </button>

            {detailLoading && (
              <p className="mb-3 text-sm text-slate-500">Đang tải chi tiết incident...</p>
            )}
            {detailError && (
              <p className="mb-3 text-sm text-red-600">
                {detailError} — đang hiển thị dữ liệu tạm thời từ bảng danh sách.
              </p>
            )}

            {detail && <IncidentDetailPage mode="view" data={detail} />}
          </div>
        </div>
      )}
    </>
  );
}