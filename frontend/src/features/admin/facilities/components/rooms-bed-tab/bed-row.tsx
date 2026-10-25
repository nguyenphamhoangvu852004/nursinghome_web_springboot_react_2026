import { UserPlus, BedDouble } from "lucide-react";
import type { Bed } from "@/services/rooms-api";

interface BedRowProps {
  bed: Bed;
  onEdit: (bed: Bed) => void;
  index: number;
}

export const BedRow = ({ bed, onEdit, index }: BedRowProps) => {
  return (
    <tr className={`hover:bg-primary/5 transition-colors ${index % 2 !== 0 ? 'bg-surface-bright/30' : ''}`}>
      <td className="p-[12px_16px] pl-12 text-on-surface font-medium">
        <div className="flex items-center gap-2">
          <BedDouble className="w-[18px] h-[18px] text-primary" />
          {bed.bed_number}
        </div>
      </td>
      <td className="p-[12px_16px]">
        {bed.status === "OCCUPIED" ? (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-error font-medium border border-error/20 bg-error/10">
            <span className="w-1.5 h-1.5 rounded-full bg-error"></span>
            Occupied
          </span>
        ) : bed.status === "MAINTENANCE" ? (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-tertiary font-medium border border-tertiary/20 bg-tertiary/10">
            <span className="w-1.5 h-1.5 rounded-full bg-tertiary"></span>
            Maintenance
          </span>
        ) : (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-primary font-medium border border-primary/20 bg-primary/10">
            <span className="w-1.5 h-1.5 rounded-full bg-primary"></span>
            Available
          </span>
        )}
      </td>
      <td className="p-[12px_16px] text-on-surface">
        {bed.resident_name ? (
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-primary/10 text-primary flex items-center justify-center text-label-md font-bold">
              {bed.resident_name.split(" ").map((n: string) => n[0]).join("")}
            </div>
            {bed.resident_name}
          </div>
        ) : (
          <span className="text-on-surface-variant/70 italic">--</span>
        )}
      </td>
      <td className="p-[12px_16px] text-on-surface-variant">
        {bed.admission_date || <span className="text-on-surface-variant/70 italic">--</span>}
      </td>
      <td className="p-[12px_16px] text-right">
        <div className="flex items-center justify-end gap-2">
          <button className="p-2 text-on-surface-variant hover:text-primary hover:bg-primary/10 rounded-md transition-colors tooltip-trigger" title="Assign Bed">
            <UserPlus className="w-[18px] h-[18px]" />
          </button>
          <button onClick={() => onEdit(bed)} className="p-2 text-on-surface-variant hover:text-primary hover:bg-primary/10 rounded-md transition-colors font-medium" title="Edit Bed">
            Edit
          </button>
        </div>
      </td>
    </tr>
  );
};
