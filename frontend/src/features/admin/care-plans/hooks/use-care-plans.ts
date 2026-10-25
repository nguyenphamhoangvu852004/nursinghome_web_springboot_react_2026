import { useQuery } from "@tanstack/react-query";
import { useSearchParams } from "react-router";
import { getCarePlansV2 } from "@/services/care-plan/care-plan-v2-services";
import { carePlanStates, carePlanVersionStatuses } from "@/services/care-plan/care-plan-v2-types";
import type { CarePlanState, CarePlanVersionStatus } from "@/services/care-plan/care-plan-v2-types";

export const carePlanSortOptions = { updated: "Recently updated", newest: "Newest first", resident: "Resident A–Z" } as const;
export interface CarePlanFilters {
  search: string;
  state: CarePlanState | "";
  versionStatus: CarePlanVersionStatus | "";
  sort: keyof typeof carePlanSortOptions;
  page: number;
  size: number;
}

export function useCarePlans() {
  const [params, setParams] = useSearchParams();
  const rawPage = Number(params.get("page") ?? 1);
  const rawSize = Number(params.get("size") ?? 10);
  const state = params.get("state") ?? "";
  const status = params.get("versionStatus") ?? "";
  const sort = params.get("sort") ?? "updated";
  const filters: CarePlanFilters = {
    search: (params.get("search") ?? "").slice(0, 200),
    state: Object.hasOwn(carePlanStates, state) ? state as CarePlanState : "",
    versionStatus: Object.hasOwn(carePlanVersionStatuses, status) ? status as CarePlanVersionStatus : "",
    sort: Object.hasOwn(carePlanSortOptions, sort) ? sort as CarePlanFilters["sort"] : "updated",
    page: Number.isSafeInteger(rawPage) && rawPage > 0 && rawPage <= 2147483647 ? rawPage : 1,
    size: [10, 20, 50].includes(rawSize) ? rawSize : 10,
  };
  const queryParams = {
    page: filters.page - 1, size: filters.size,
    search: filters.search || undefined, state: filters.state || undefined,
    versionStatus: filters.versionStatus || undefined,
    sortBy: filters.sort === "resident" ? "residentName" as const
      : filters.sort === "newest" ? "createdAt" as const : "updatedAt" as const,
    sortDir: filters.sort === "resident" ? "ASC" as const : "DESC" as const,
  };
  const query = useQuery({
    queryKey: ["care-plans", "v2", queryParams],
    queryFn: ({ signal }) => getCarePlansV2(queryParams, signal),
  });
  function updateFilters(patch: Partial<CarePlanFilters>) {
    setParams((previous) => {
      const next = new URLSearchParams(previous);
      for (const [key, value] of Object.entries(patch)) {
        if (value === "") next.delete(key);
        else next.set(key, String(value));
      }
      if (!("page" in patch)) next.delete("page");
      return next;
    });
  }
  return { query, filters, updateFilters, resetFilters: () => setParams({}) };
}
