import React from "react";
import { ChevronDown } from "lucide-react";

export function FilterDropdown({ label }: { label: string }) {
  return (
    <button className="inline-flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-2 text-sm text-slate-600 shadow-sm hover:bg-slate-50">
      {label}
      <ChevronDown className="size-4 text-slate-400" />
    </button>
  );
}
