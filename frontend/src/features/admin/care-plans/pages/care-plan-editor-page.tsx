import { useState } from "react";
import { Link, useParams } from "react-router";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { getPlanContent } from "@/services/care-plan/care-plan-content";
import { CareContentForm } from "../components/care-content-form";

export default function CarePlanEditorPage() {
  const { id } = useParams();
  const planId = Number(id);
  const validId = Number.isSafeInteger(planId) && planId > 0;
  const client = useQueryClient();
  const [versionId, setVersionId] = useState<number | null>(null);
  const [form, setForm] = useState<{ kind: "goal" | "intervention"; parentId: number } | null>(null);
  const [notice, setNotice] = useState("");
  const query = useQuery({ queryKey: ["care-plan-content", planId],
    queryFn: ({ signal }) => getPlanContent(planId, signal), enabled: validId });
  const plan = query.data;
  const version = plan?.versions.find(item => item.id === versionId) ?? plan?.versions[0];
  const editable = plan?.state === "OPEN" && version?.status === "DRAFT";
  function saved() {
    setForm(null); setNotice("Saved successfully.");
    void client.invalidateQueries({ queryKey: ["care-plan-content", planId] });
    void client.invalidateQueries({ queryKey: ["care-plans", "v2"] });
  }
  return <main className="min-h-full bg-slate-50 px-4 py-8 sm:px-8">
    <div className="mx-auto max-w-5xl space-y-6">
      <Link to="/admin/care-plans" className="text-sm text-primary hover:underline">← Back to care plans</Link>
      <header><p className="text-xs font-semibold uppercase tracking-widest text-primary">Care planning</p><h1 className="mt-2 text-3xl font-semibold">Care plan #{id}</h1><p className="mt-2 text-muted-foreground">Build goals and the interventions that support them.</p></header>
      {!validId ? <p role="alert">Invalid care plan ID.</p> : query.isPending ? <p role="status">Loading care plan…</p>
        : query.isLoadingError ? <div role="alert" className="space-y-3 rounded-xl border bg-white p-6"><p>{query.error.message}</p><Button onClick={() => void query.refetch()}>Try again</Button></div>
          : plan && <>
            <section className="flex flex-wrap items-center justify-between gap-4 rounded-xl border bg-white p-5">
              <label className="space-y-1 text-sm font-medium">Version<select disabled={!!form} value={version?.id ?? ""} onChange={event => { setVersionId(Number(event.target.value)); setNotice(""); }} className="block rounded-lg border p-2">
                {plan.versions.map(item => <option key={item.id} value={item.id}>Version {item.versionNumber || "—"} · #{item.id} · {item.status}</option>)}
              </select></label>
              <span className="text-sm text-muted-foreground">Plan: {plan.state} · {version?.goals.length ?? 0} goals</span>
              {editable && <Button disabled={!!form} onClick={() => { setNotice(""); setForm({ kind: "goal", parentId: version!.id }); }}>+ Add goal</Button>}
            </section>
            <p className="text-sm text-muted-foreground">{editable ? "Nurse and DON can add content to this draft. Tasks are generated through the later care delivery workflow." : "This version is read-only. Adding content requires an open plan and a draft version."}</p>
            {notice && <p role="status" className="rounded-lg bg-emerald-50 p-3 text-emerald-800">{notice}</p>}
            {query.isRefetchError && <p role="alert" className="text-red-700">Unable to refresh saved content. <button className="underline" onClick={() => void query.refetch()}>Refresh</button></p>}
            {form && editable && <CareContentForm key={`${form.kind}-${form.parentId}`} {...form} onSaved={saved} onCancel={() => setForm(null)} />}
            {!version ? <p className="rounded-xl border border-dashed p-8 text-center">No versions available.</p> : version.goals.length === 0 ? <p className="rounded-xl border border-dashed p-8 text-center text-muted-foreground">No goals yet. Add the first goal to start building this draft.</p>
              : version.goals.map(({ goal, interventions }) => <article key={goal.id} className="overflow-hidden rounded-xl border bg-white shadow-sm">
                <header className="flex flex-wrap items-start justify-between gap-3 border-b p-5"><div><p className="text-xs font-medium text-primary">{goal.priority} priority · {goal.status}</p><h2 className="mt-1 text-xl font-semibold">{goal.title}</h2><p className="mt-1 text-sm text-muted-foreground">Goal #{goal.id}{goal.targetDate ? ` · Target: ${goal.targetDate}` : ""}</p></div>
                  {editable && <Button variant="outline" disabled={!!form} onClick={() => { setNotice(""); setForm({ kind: "intervention", parentId: goal.id }); }}>+ Add intervention</Button>}
                </header>
                <dl className="grid gap-4 p-5 text-sm sm:grid-cols-2">{[["Problem / need", goal.problemOrNeed], ["Desired outcome", goal.desiredOutcome], ["Baseline", goal.baselineValue], ["Target", [goal.targetValue, goal.targetUnit].filter(Boolean).join(" ")], ["Measurement", goal.measurementMethod], ["Resident preference", goal.residentPreference]].filter(([, value]) => value).map(([label, value]) => <div key={label}><dt className="font-medium text-muted-foreground">{label}</dt><dd className="mt-1 whitespace-pre-wrap break-words">{value}</dd></div>)}</dl>
                <section className="space-y-3 bg-slate-50 p-5"><h3 className="text-sm font-semibold">Interventions ({interventions.length})</h3>{interventions.length === 0 ? <p className="text-sm text-muted-foreground">No interventions for this goal.</p> : interventions.map(item => <div key={item.id} className="rounded-lg border bg-white p-4"><h4 className="font-medium">{item.title}</h4><p className="mt-2 whitespace-pre-wrap break-words text-sm">{item.instructions}</p><p className="mt-3 text-xs text-muted-foreground">{item.assignedRole} · {item.frequencyCode}{item.responsibleDiscipline ? ` · ${item.responsibleDiscipline}` : ""} · Order {item.sortOrder} · {item.requiresTaskGeneration ? "Tasks required" : "Guidance only"}</p>{(item.startDate || item.endDate) && <p className="mt-2 text-xs">{item.startDate || "No start date"} → {item.endDate || "No end date"}</p>}{[["Schedule", item.scheduleDefinition], ["As-needed criteria", item.prnCriteria], ["Rationale", item.evidenceOrRationale]].filter(([, value]) => value).map(([label, value]) => <p key={label} className="mt-2 whitespace-pre-wrap break-words text-sm"><span className="font-medium">{label}: </span>{value}</p>)}</div>)}</section>
              </article>)}
          </>}
    </div>
  </main>;
}
