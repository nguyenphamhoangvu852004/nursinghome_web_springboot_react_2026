import {
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";

export const ResidentTaskTableHeader = () => {
  return (
    <TableHeader>
      <TableRow className="bg-muted/40 hover:bg-muted/40 border-b border-border">
        {/* Assigned Staff replaces Resident — 240px */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground w-[240px] min-w-[200px]">
          Assigned Staff
        </TableHead>
        {/* Task Type — 130px */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground w-[130px]">
          Task Type
        </TableHead>
        {/* Goal / Intervention — auto, fills remaining space */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground">
          Goal / Intervention
        </TableHead>
        {/* Time — 110px, centred */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground text-center w-[110px]">
          Scheduled
        </TableHead>
        {/* Status — 110px, centred */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground text-center w-[110px]">
          Status
        </TableHead>
        {/* Flags — 70px, centred */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground text-center w-[70px]">
          Flags
        </TableHead>
        {/* Actions — 160px, right-aligned */}
        <TableHead className="h-10 px-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground text-right w-[160px]">
          Actions
        </TableHead>
      </TableRow>
    </TableHeader>
  );
};
