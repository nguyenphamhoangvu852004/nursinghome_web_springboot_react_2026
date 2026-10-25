import { Link } from "react-router";
import { Loader2, Plus, RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import CarePlanOptions from "../components/care-plan-options";
import CarePlanStatistical from "../components/care-plan-statistical";
import CarePlanTable from "../components/care-plan-table";
import { useCarePlans } from "../hooks/use-care-plans";

export default function CarePlanPage() {
  const { query, filters, updateFilters, resetFilters } = useCarePlans();
  const response = query.data;
  const metadata = response?.metadata;
  const first = metadata && response.data.list.length ? (metadata.currentPage - 1) * metadata.currentLimit + 1 : 0;
  const last = first ? first + (response?.data.list.length ?? 0) - 1 : 0;

  return (
    <main className="min-h-full bg-slate-50 px-4 py-8 text-slate-900 sm:px-8 lg:px-12">
      <div className="mx-auto max-w-[1500px] space-y-6">
        <header className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="mb-2 text-xs font-semibold uppercase tracking-widest text-primary">Care planning</p>
            <h1 className="text-3xl font-semibold tracking-tight">Care plans</h1>
            <p className="mt-2 text-sm text-muted-foreground">Track plans by admission, follow draft progress and see which version is effective.</p>
          </div>
          <Link to="/admin/care-plans/new" className="inline-flex h-10 items-center gap-2 rounded-lg bg-primary px-4 text-sm font-medium text-primary-foreground hover:bg-primary/90"><Plus className="size-4" /> New care plan</Link>
        </header>
        <CarePlanOptions filters={filters} onChange={updateFilters} onReset={resetFilters} />
        <CarePlanStatistical statistics={query.isError ? undefined : response?.data.statistics} />
        <p className="text-xs text-muted-foreground">Statistics cover all matching plans. Draft and review counts include open plans only; an open plan may also have an effective version.</p>

        <section aria-label="Care plan results" aria-busy={query.isFetching} className="overflow-hidden rounded-2xl border bg-white shadow-sm">
          <div className="flex flex-wrap items-center justify-between gap-3 border-b px-5 py-4">
            <div><h2 className="font-semibold">Plan directory</h2><p className="mt-1 text-xs text-muted-foreground">Goals and interventions are counted from the latest version.</p></div>
            <Button variant="outline" disabled={query.isFetching} onClick={() => void query.refetch()}><RefreshCw className={`size-4 ${query.isFetching ? "animate-spin" : ""}`} /> Refresh</Button>
          </div>
          {query.isError ? (
            <div role="alert" className="space-y-3 p-8 text-center"><p className="font-medium text-red-700">Unable to load care plans</p><p className="text-sm text-muted-foreground">{query.error.message}</p><Button variant="outline" onClick={() => void query.refetch()}>Try again</Button></div>
          ) : query.isPending ? (
            <div role="status" className="flex h-56 items-center justify-center gap-2 text-sm text-muted-foreground"><Loader2 className="size-5 animate-spin" /> Loading care plans…</div>
          ) : response && metadata && <>
            <CarePlanTable carePlans={response.data.list} />
            <footer className="flex flex-wrap items-center justify-between gap-4 border-t px-5 py-4 text-sm">
              <p aria-live="polite" className="text-muted-foreground">{first}–{last} of {metadata.totalElements.toLocaleString()} plans</p>
              <div className="flex flex-wrap items-center gap-3">
                <label className="flex items-center gap-2 text-xs text-muted-foreground">Rows per page
                  <select className="h-8 rounded-lg border bg-white px-2" value={filters.size} onChange={(event) => updateFilters({ size: Number(event.target.value) })}>
                    {[10, 20, 50].map((size) => <option key={size} value={size}>{size}</option>)}
                  </select>
                </label>
                <Button variant="outline" disabled={!metadata.hasPrevious || query.isFetching} onClick={() => updateFilters({ page: Math.max(1, filters.page - 1) })}>Previous</Button>
                <span className="text-xs text-muted-foreground">Page {metadata.currentPage} of {Math.max(1, metadata.totalPage)}</span>
                <Button variant="outline" disabled={!metadata.hasNext || query.isFetching} onClick={() => updateFilters({ page: filters.page + 1 })}>Next</Button>
                {metadata.totalPage > 0 && filters.page > metadata.totalPage && <Button variant="ghost" onClick={() => updateFilters({ page: 1 })}>First page</Button>}
              </div>
            </footer>
          </>}
        </section>
      </div>
    </main>
  );
}
