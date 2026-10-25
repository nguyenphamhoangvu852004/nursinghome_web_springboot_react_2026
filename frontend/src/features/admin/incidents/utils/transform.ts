import type { IncidentApiResponse } from "@/services/incidents/incidents-api";

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

export function toIncidentRow(api: IncidentApiResponse) {
  const room = api.resident.bed?.room?.roomNumber;
  const resident = room ? `${api.resident.displayName} · ${room}` : api.resident.displayName;

  let sla = "—";
  let slaStyle = "text-slate-400";
  if (api.slaCountDown !== null && api.slaCountDown !== undefined) {
    if (api.slaCountDown < 0) {
      sla = "OVERDUE";
      slaStyle = "text-red-600";
    } else {
      sla = `${api.slaCountDown}h left`;
      slaStyle = api.slaCountDown <= 24 ? "text-red-600" : "text-emerald-600";
    }
  }

  return {
    id: api.id,
    resident,
    type: typeLabels[api.incidentType] ?? api.incidentType,
    severity: api.severity.levelName,
    reported: api.reportedAt ? api.reportedAt.replace("T", " ").slice(0, 16) : "",
    sla,
    slaStyle,
    status: statusLabels[api.status] ?? api.status,
    chart: api.isLocked ? "Locked" : "Unlocked",
  };
}