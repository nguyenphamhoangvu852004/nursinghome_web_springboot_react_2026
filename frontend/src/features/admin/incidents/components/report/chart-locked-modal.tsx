"use client";

import * as React from "react";
import { Lock, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";

export type ChartLockedModalProps = {
  open: boolean;
  residentName: string;
  lockedAt: string; // e.g. "2026-07-03 09:22"
  lockRule?: string; // e.g. "BR-07"
  unlockRule?: string; // e.g. "LC-06"
  incidentId: string | number; // e.g. "INC-2044"
  onBackToProfile: () => void;
  onViewIncident: () => void;
};

export function ChartLockedModal({
  open,
  residentName,
  lockedAt,
  lockRule = "BR-07",
  unlockRule = "LC-06",
  incidentId,
  onBackToProfile,
  onViewIncident,
}: ChartLockedModalProps) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-6">
      <div className="w-full max-w-md rounded-[20px] border border-gray-100 bg-white p-8 text-center shadow-lg">
        <div className="mx-auto mb-5 flex size-16 items-center justify-center rounded-full bg-red-100">
          <Lock className="size-7 text-red-500" />
        </div>

        <h2 className="text-xl font-bold text-slate-900">Chart Locked</h2>
        <p className="mx-auto mt-2 max-w-xs text-sm text-slate-500">
          {residentName}&apos;s chart was automatically locked at {lockedAt} ({lockRule}).
        </p>

        <div className="mt-6 rounded-xl bg-slate-50 p-4 text-left">
          <div className="flex items-center justify-between">
            <span className="text-sm text-slate-500">Incident reference</span>
            <span className="text-sm font-bold text-slate-900">#{incidentId}</span>
          </div>
          <p className="mt-2 text-sm text-slate-600">
            M1, M2 and M3 records for this resident are now read-only until a DON unlocks the chart ({unlockRule}).
          </p>
        </div>

        <div className="mt-6 flex gap-3">
          <Button variant="outline" className="flex-1 justify-center" onClick={onBackToProfile}>
            Back to Resident Profile
          </Button>
          <Button className="flex-1 justify-center gap-1.5 bg-blue-600 text-white hover:bg-blue-700" onClick={onViewIncident}>
            View Incident
            <ArrowRight className="size-4" />
          </Button>
        </div>
      </div>
    </div>
  );
}