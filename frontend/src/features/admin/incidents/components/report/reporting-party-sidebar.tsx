"use client";

export function ReportingPartySidebar() {
  return (
    <div className="space-y-4">

      {/* Reporting Party */}
      <div className="rounded-md border border-border bg-white p-5">
        <h2 className="mb-4 text-lg font-semibold">
          Reporting Party
        </h2>

        <div>
          <p className="text-sm font-semibold">
            Anna Lee, RN
          </p>

          <p className="mt-1 text-xs text-muted-foreground">
            Captured automatically
          </p>
        </div>

        <div className="mt-5 flex items-center justify-between border-t pt-3">
          <span className="text-xs text-muted-foreground">
            Timestamp
          </span>

          <span className="text-sm font-semibold">
            2026-07-03 09:22
          </span>
        </div>
      </div>

      {/* What happens next */}
      <div className="rounded-md border border-border bg-white p-5">
        <h3 className="mb-4 text-lg font-semibold">
          What happens next
        </h3>

        <ol className="space-y-2 text-sm text-muted-foreground">
          <li>1. Chart auto-locks (BR-07)</li>
          <li>2. DON is notified</li>
          <li>3. SLA countdown starts (24–48h, NFR-06)</li>
          <li>4. Incident enters review queue</li>
        </ol>
      </div>

      {/* Warning */}
      <div className="rounded-md border border-amber-300 bg-amber-100 p-4">
        <p className="text-sm font-semibold leading-6 text-amber-800">
          Submitting will lock this resident&apos;s chart until DON review
          (LC-06).
        </p>
      </div>

    </div>
  );
}