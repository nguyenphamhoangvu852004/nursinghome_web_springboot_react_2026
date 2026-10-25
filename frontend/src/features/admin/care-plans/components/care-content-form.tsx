import { useRef, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { createGoal, createIntervention } from "@/services/care-plan/care-plan-content";
import type { GoalInput, InterventionInput } from "@/services/care-plan/care-plan-content";

type Field = { name: string; label: string; max?: number; required?: boolean; type?: string; multiline?: boolean };
const goalFields: Field[] = [
  { name: "title", label: "Goal title", required: true, max: 255 },
  { name: "problemOrNeed", label: "Problem or need", required: true, max: 1000, multiline: true },
  { name: "desiredOutcome", label: "Desired outcome", required: true, max: 1000, multiline: true },
  { name: "baselineValue", label: "Current baseline", required: true, max: 2000, multiline: true },
  { name: "targetValue", label: "Target value", max: 255 }, { name: "targetUnit", label: "Target unit", max: 50 },
  { name: "measurementMethod", label: "Measurement method", max: 500 },
  { name: "targetDate", label: "Target date", type: "date" },
  { name: "residentPreference", label: "Resident preference", max: 1000, multiline: true },
];
const interventionFields: Field[] = [
  { name: "title", label: "Intervention title", required: true, max: 255 },
  { name: "instructions", label: "Instructions", required: true, max: 2000, multiline: true },
  { name: "responsibleDiscipline", label: "Responsible discipline", max: 50 },
  { name: "assignedRole", label: "Assigned role", required: true, max: 50 },
  { name: "frequencyCode", label: "Frequency code", required: true, max: 30 },
  { name: "scheduleDefinition", label: "Schedule definition", max: 1000, multiline: true },
  { name: "startDate", label: "Start date", type: "date" }, { name: "endDate", label: "End date", type: "date" },
  { name: "prnCriteria", label: "As-needed criteria", max: 1000, multiline: true },
  { name: "evidenceOrRationale", label: "Evidence or rationale", max: 1000, multiline: true },
];

export function CareContentForm({ kind, parentId, onSaved, onCancel }: {
  kind: "goal" | "intervention"; parentId: number; onSaved: () => void; onCancel: () => void;
}) {
  const [busy, setBusy] = useState(false);
  const lock = useRef(false);
  const [error, setError] = useState("");
  const fields = kind === "goal" ? goalFields : interventionFields;
  return <form className="space-y-5 rounded-xl border bg-slate-50 p-5" onSubmit={async (event) => {
    event.preventDefault();
    if (lock.current) return;
    const form = new FormData(event.currentTarget);
    const values: Record<string, string> = {};
    for (const field of fields) {
      const value = String(form.get(field.name) ?? "").trim();
      if (field.required && !value) { setError(`${field.label} is required.`); return; }
      if (value) values[field.name] = value;
    }
    if (values.startDate && values.endDate && values.endDate < values.startDate) {
      setError("End date must be on or after start date."); return;
    }
    lock.current = true; setBusy(true); setError("");
    try {
      if (kind === "goal") await createGoal(parentId, { ...values, priority: String(form.get("priority")) } as unknown as GoalInput);
      else await createIntervention(parentId, { ...values, requiresTaskGeneration: form.get("requiresTaskGeneration") === "on",
        sortOrder: Number(form.get("sortOrder") || 0) } as unknown as InterventionInput);
      onSaved();
    } catch (cause) { setError(cause instanceof Error ? cause.message : "Unable to save. Please try again."); }
    finally { lock.current = false; setBusy(false); }
  }}>
    <h3 className="text-lg font-semibold">Add {kind}</h3>
    {error && <p role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-700">{error}</p>}
    <fieldset disabled={busy} className="grid gap-4 sm:grid-cols-2">
      {fields.map(field => <label key={field.name} className={`space-y-1 text-sm font-medium ${field.multiline ? "sm:col-span-2" : ""}`}>
        <span>{field.label}{field.required ? " *" : ""}</span>
        {field.multiline ? <textarea name={field.name} required={field.required} maxLength={field.max} rows={3} className="block w-full rounded-lg border bg-white p-2 font-normal" />
          : <Input name={field.name} required={field.required} maxLength={field.max} type={field.type || "text"} className="bg-white" />}
      </label>)}
      {kind === "goal" ? <label className="space-y-1 text-sm font-medium">Priority<select name="priority" defaultValue="LOW" className="block w-full rounded-lg border bg-white p-2"><option value="LOW">Low</option><option value="MEDIUM">Medium</option><option value="HIGH">High</option></select></label>
        : <><label className="space-y-1 text-sm font-medium">Display order<Input name="sortOrder" type="number" step="1" defaultValue="0" className="bg-white" /></label>
          <label className="flex items-center gap-2 text-sm"><input name="requiresTaskGeneration" type="checkbox" defaultChecked /> Generate tasks when the plan becomes effective</label></>}
    </fieldset>
    <div className="flex gap-2"><Button type="submit" disabled={busy}>{busy ? "Saving…" : `Save ${kind}`}</Button><Button type="button" variant="outline" disabled={busy} onClick={onCancel}>Cancel</Button></div>
  </form>;
}
