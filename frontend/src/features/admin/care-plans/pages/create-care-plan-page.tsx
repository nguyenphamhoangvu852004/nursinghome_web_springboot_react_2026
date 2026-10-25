import { useRef, useState } from "react";
import { Link } from "react-router";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, ArrowRight, Check, CheckCircle2, ClipboardList, Loader2, Search } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { createCarePlanV2, getCarePlanAdmissions } from "@/services/care-plan/care-plan-v2-services";
import type { AdmissionResponse } from "@/features/admin/admissions/types/admission-type";
import { AdmissionSummary } from "../components/create-care-plan/admission-summary";

export default function CreateCarePlanPage() {
  const queryClient = useQueryClient();
  const [step, setStep] = useState(0);
  const [search, setSearch] = useState("");
  const [selected, setSelected] = useState<AdmissionResponse | null>(null);
  const [confirmed, setConfirmed] = useState(false);
  const [validationError, setValidationError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const submitting = useRef(false);
  const admissions = useQuery({
    queryKey: ["care-plan-admissions"],
    queryFn: ({ signal }) => getCarePlanAdmissions(signal),
    staleTime: 0,
  });
  const create = useMutation({
    mutationFn: createCarePlanV2,
    retry: false,
    onSuccess: () => { void queryClient.invalidateQueries({ queryKey: ["care-plans", "v2"] }); },
  });
  const filtered = admissions.data?.filter((admission) =>
    `${admission.residentName} ${admission.residentId} ${admission.id}`
      .toLowerCase().includes(search.trim().toLowerCase()),
  ) ?? [];

  async function handleCreate() {
    if (!selected || !confirmed || submitting.current || create.isSuccess) return;
    submitting.current = true;
    try {
      // Recheck the admission before writing: it may have been discharged meanwhile.
      const fresh = await admissions.refetch();
      if (fresh.error) throw fresh.error;
      if (!fresh.data?.some((item) => item.id === selected.id && item.residentId === selected.residentId)) {
        setStep(0);
        setSelected(null);
        setConfirmed(false);
        setValidationError("This admission is no longer active. Select another admission.");
        return;
      }
      await create.mutateAsync({ residentId: selected.residentId, admissionId: selected.id });
    } catch (error) {
      setValidationError(error instanceof Error ? error.message : "Unable to create the care plan. Please try again.");
    } finally {
      submitting.current = false;
      setBusy(false);
    }
  }
  const locked = busy || create.isPending;

  return (
    <main className="min-h-full bg-slate-50 px-4 py-8 text-slate-900 sm:px-8 lg:px-12">
      <div className="mx-auto max-w-6xl space-y-7">
        {!locked && <Link to="/admin/care-plans" className="inline-flex items-center gap-2 text-sm text-muted-foreground hover:text-primary"><ArrowLeft className="size-4" /> Back to care plans</Link>}
        <header>
          <p className="mb-2 text-xs font-semibold uppercase tracking-widest text-primary">Care planning</p>
          <h1 className="text-3xl font-semibold tracking-tight">New care plan</h1>
          <p className="mt-2 text-muted-foreground">Start a care plan for an active admission with an initial draft.</p>
        </header>

        {create.isSuccess && selected ? (
          <section aria-live="polite" className="rounded-2xl border bg-white p-8 shadow-sm sm:p-12">
            <CheckCircle2 className="mb-5 size-12 text-emerald-600" />
            <h2 className="text-2xl font-semibold">Care plan created</h2>
            <p className="mt-2 text-muted-foreground">Care plan #{create.data.id} has been created for {selected.residentName} with an initial draft.</p>
            <div className="my-8 rounded-xl border bg-slate-50 p-6"><AdmissionSummary admission={selected} /></div>
            <p className="mb-6 text-sm text-muted-foreground">The draft still needs care goals, interventions and review before it can be used for care delivery.</p>
            <div className="flex gap-5"><Link to={`/admin/care-plans/${create.data.id}/edit`} className="font-medium text-primary hover:underline">Add goals & interventions →</Link><Link to="/admin/care-plans" className="font-medium text-primary hover:underline">Return to care plans</Link></div>
          </section>
        ) : (
          <>
            <ol aria-label="Creation progress" className="flex gap-6 border-b pb-5">
              {["Select admission", "Review & create"].map((label, index) => (
                <li key={label} aria-current={step === index ? "step" : undefined} className={`flex items-center gap-2 text-sm ${step === index ? "font-semibold text-primary" : "text-muted-foreground"}`}>
                  <span className={`flex size-7 items-center justify-center rounded-full ${step === index ? "bg-primary text-primary-foreground" : "bg-slate-200"}`}>{step > index ? <Check className="size-4" /> : index + 1}</span>{label}
                </li>
              ))}
            </ol>
            <div className="grid items-start gap-6 lg:grid-cols-[1fr_300px]">
              <section className="overflow-hidden rounded-2xl border bg-white shadow-sm">
                <div className="border-b p-6">
                  <h2 className="text-lg font-semibold">{step === 0 ? "Who is this care plan for?" : "Review the care plan"}</h2>
                  <p className="mt-1 text-sm text-muted-foreground">{step === 0 ? "Select the resident’s active admission. Resident details are filled in automatically." : "Confirm the resident and admission before creating the draft."}</p>
                </div>
                <div className="space-y-5 p-6">
                  {validationError && <div role="alert" className="rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-700">{validationError}</div>}
                  {step === 0 ? (
                    <>
                      <label className="block space-y-2 text-sm font-medium">
                        <span>Find a resident or admission</span>
                        <div className="relative"><Search className="absolute top-3 left-3 size-4 text-muted-foreground" /><Input className="h-10 pl-9" placeholder="Search by name, resident ID or admission ID" value={search} onChange={(event) => setSearch(event.target.value)} /></div>
                      </label>
                      {admissions.isLoading ? <p role="status" className="flex items-center gap-2 py-8 text-sm text-muted-foreground"><Loader2 className="size-4 animate-spin" /> Loading active admissions…</p>
                        : admissions.isError ? <div role="alert" className="space-y-3"><p className="text-sm text-red-700">Unable to load admissions. {admissions.error.message}</p><Button variant="outline" onClick={() => void admissions.refetch()}>Try again</Button></div>
                        : filtered.length === 0 ? <p role="status" className="rounded-xl border border-dashed p-8 text-center text-sm text-muted-foreground">{search ? "No admissions match your search." : "No active admissions are available. Complete an admission first."}</p>
                        : <fieldset className="max-h-96 space-y-3 overflow-y-auto p-1"><legend className="sr-only">Active admissions</legend>{filtered.map((admission) => (
                          <label key={admission.id} className={`flex cursor-pointer items-start gap-3 rounded-xl border p-4 transition-colors ${selected?.id === admission.id ? "border-primary bg-primary/5" : "hover:bg-slate-50"}`}>
                            <input type="radio" name="admission" className="mt-1 accent-current" checked={selected?.id === admission.id} onChange={() => { setSelected(admission); setValidationError(null); setConfirmed(false); }} />
                            <span className="min-w-0 flex-1"><span className="block font-medium break-words">{admission.residentName}</span><span className="mt-1 block text-xs text-muted-foreground">Resident #{admission.residentId} · Admission #{admission.id}</span><span className="mt-1 block text-xs text-muted-foreground">Admitted {admission.admissionDate.slice(0, 10)} · Facility #{admission.facilityId}</span></span>
                            <span className="rounded-full bg-emerald-50 px-2 py-1 text-xs font-medium text-emerald-700">Active</span>
                          </label>
                        ))}</fieldset>}
                    </>
                  ) : selected && (
                    <>
                      <AdmissionSummary admission={selected} />
                      <div className="rounded-xl bg-blue-50 p-4 text-sm text-blue-900"><p className="font-medium">An initial draft will be created</p><p className="mt-1">Add care goals and interventions during drafting, then complete the review process before care delivery.</p></div>
                      <label className="flex cursor-pointer items-start gap-3 text-sm"><input type="checkbox" className="mt-1" checked={confirmed} disabled={locked} onChange={(event) => setConfirmed(event.target.checked)} /><span>I have checked the resident and admission details above.</span></label>
                    </>
                  )}
                </div>
                <footer className="flex flex-wrap items-center justify-between gap-3 border-t bg-slate-50/70 px-6 py-4">
                  {step === 0 ? <Link to="/admin/care-plans" className="text-sm text-muted-foreground hover:underline">Cancel</Link> : <Button variant="outline" disabled={locked} onClick={() => { setStep(0); setValidationError(null); }}>Back</Button>}
                  {step === 0 ? <Button size="lg" disabled={!selected || admissions.isError || admissions.isFetching} onClick={() => { setStep(1); setValidationError(null); }}>Review details <ArrowRight className="size-4" /></Button> : <Button size="lg" disabled={!confirmed || locked} onClick={() => { setBusy(true); setValidationError(null); void handleCreate(); }}>{locked && <Loader2 className="size-4 animate-spin" />}{locked ? "Creating draft…" : "Create care plan"}</Button>}
                </footer>
              </section>
              <aside className="space-y-5 rounded-2xl border bg-white p-6">
                <ClipboardList className="size-8 text-primary" />
                <h2 className="font-semibold">From admission to care</h2>
                <ol className="space-y-4 text-sm">
                  <li><p className="font-medium">1. Create a draft</p><p className="mt-1 text-muted-foreground">Link the plan to the correct resident and admission.</p></li>
                  <li><p className="font-medium">2. Build the care plan</p><p className="mt-1 text-muted-foreground">Define goals and the interventions needed to achieve them.</p></li>
                  <li><p className="font-medium">3. Review and approve</p><p className="mt-1 text-muted-foreground">Complete the review before applying the plan.</p></li>
                </ol>
                <p className="border-t pt-4 text-xs leading-relaxed text-muted-foreground">Each admission should have only one open care plan. If a plan already exists, continue working on that plan.</p>
              </aside>
            </div>
          </>
        )}
      </div>
    </main>
  );
}
