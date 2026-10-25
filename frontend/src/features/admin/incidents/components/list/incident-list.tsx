"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { AlarmClock, Clock, FileCheck2, Layers } from "lucide-react";
import { CardSummary, FilterDropdown, IncidentsTable, Pagination } from ".";
import { incidentsApi } from "@/services/incidents/incidents-api";
import { toIncidentRow } from "../../utils/transform";

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

const chartStyles: Record<string, string> = {
  Locked: "bg-red-100 text-red-600",
  Unlocked: "bg-emerald-100 text-emerald-700",
};

const PAGE_SIZE = 20;

export function IncidentList() {
  const [incidents, setIncidents] = useState<ReturnType<typeof toIncidentRow>[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // currentPage ở đây dùng kiểu 1-based (dành cho UI Pagination).
  // Backend Spring Data Page<T> lại đánh số "number" theo kiểu 0-based,
  // nên khi gọi API phải trừ 1, và totalElements lấy từ response chứ
  // không phải suy ra từ incidents.length (vì incidents chỉ chứa 1 trang).
  const [currentPage, setCurrentPage] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  useEffect(() => {
    setLoading(true);
    setError(null);

    incidentsApi
      .getAll(currentPage - 1, PAGE_SIZE)
      .then((page) => {
        setIncidents(page.content.map(toIncidentRow));
        setTotalItems(page.totalElements);
      })
      .catch((e) => setError(e?.response?.data?.message ?? e.message))
      .finally(() => setLoading(false));
  }, [currentPage]);

  const cards = [
    { label: "Open", value: incidents.filter((i) => i.status === "Open").length, icon: <AlarmClock className="size-5 text-amber-500" />, iconBg: "bg-amber-100" },
    { label: "Overdue (SLA)", value: incidents.filter((i) => i.sla === "OVERDUE").length, icon: <Clock className="size-5 text-red-500" />, iconBg: "bg-red-100" },
    { label: "Chart Locked", value: incidents.filter((i) => i.chart === "Locked").length, icon: <Layers className="size-5 text-pink-500" />, iconBg: "bg-pink-100" },
    { label: "Resolved (month)", value: incidents.filter((i) => i.status === "Resolved").length, icon: <FileCheck2 className="size-5 text-emerald-600" />, iconBg: "bg-emerald-100" },
  ];

  if (loading) return <p className="text-sm text-slate-500">Đang tải dữ liệu...</p>;
  if (error) return <p className="text-sm text-red-600">Lỗi tải dữ liệu: {error}</p>;

  return (
    <div className="space-y-6">
      <div>
        <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h1 className="text-3xl font-bold text-slate-900">Incidents</h1>
            <p className="mt-1 text-sm text-slate-500">{totalItems} incidents this month</p>
          </div>
          <Button className="rounded-full bg-blue-600 px-5 py-3 text-white hover:bg-blue-700">+ Report Incident</Button>
        </div>

        <div className="mt-4 flex flex-wrap gap-3">
          <FilterDropdown label="Status: All" />
          <FilterDropdown label="Severity: All" />
        </div>
      </div>

      <CardSummary cards={cards} />

      <IncidentsTable incidents={incidents} severityStyles={severityStyles} statusStyles={statusStyles} chartStyles={chartStyles}>
        <Pagination
          currentPage={currentPage}
          totalItems={totalItems}
          pageSize={PAGE_SIZE}
          onPageChange={setCurrentPage}
        />
      </IncidentsTable>
    </div>
  );
}