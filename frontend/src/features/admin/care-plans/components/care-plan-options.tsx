import { Search, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { carePlanStates, carePlanVersionStatuses } from "@/services/care-plan/care-plan-v2-types";
import { carePlanSortOptions } from "../hooks/use-care-plans";
import type { CarePlanFilters } from "../hooks/use-care-plans";

interface Props {
  filters: CarePlanFilters;
  onChange: (patch: Partial<CarePlanFilters>) => void;
  onReset: () => void;
}
const selectClass = "h-10 w-full rounded-lg border bg-white px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-primary";

export default function CarePlanOptions({ filters, onChange, onReset }: Props) {
  return (
    <div className="space-y-4 rounded-2xl border bg-white p-5 shadow-sm">
      <form className="flex flex-wrap gap-2" onSubmit={(event) => {
        event.preventDefault();
        onChange({ search: String(new FormData(event.currentTarget).get("search") ?? "").trim() });
      }}>
        <label className="relative min-w-48 flex-1">
          <span className="sr-only">Search care plans</span>
          <Search className="absolute top-3 left-3 size-4 text-muted-foreground" />
          <Input key={filters.search} name="search" defaultValue={filters.search} maxLength={200}
            placeholder="Resident name, resident ID, admission ID or plan ID" className="h-10 pl-9" />
        </label>
        <Button type="submit" className="h-10 px-4">Search</Button>
      </form>
      <div className="grid items-end gap-3 sm:grid-cols-2 xl:grid-cols-[1fr_1fr_1fr_auto]">
        <label className="space-y-2 text-xs font-medium text-muted-foreground">
          <span>Plan state</span>
          <select className={selectClass} value={filters.state}
            onChange={(event) => onChange({ state: event.target.value as CarePlanFilters["state"] })}>
            <option value="">All plan states</option>
            {Object.entries(carePlanStates).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
        </label>
        <label className="space-y-2 text-xs font-medium text-muted-foreground">
          <span>Latest version</span>
          <select className={selectClass} value={filters.versionStatus}
            onChange={(event) => onChange({ versionStatus: event.target.value as CarePlanFilters["versionStatus"] })}>
            <option value="">All version statuses</option>
            {Object.entries(carePlanVersionStatuses).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
        </label>
        <label className="space-y-2 text-xs font-medium text-muted-foreground">
          <span>Sort by</span>
          <select className={selectClass} value={filters.sort}
            onChange={(event) => onChange({ sort: event.target.value as CarePlanFilters["sort"] })}>
            {Object.entries(carePlanSortOptions).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
        </label>
        <Button variant="ghost" className="h-10" onClick={onReset}><X className="size-4" /> Reset filters</Button>
      </div>
    </div>
  );
}
