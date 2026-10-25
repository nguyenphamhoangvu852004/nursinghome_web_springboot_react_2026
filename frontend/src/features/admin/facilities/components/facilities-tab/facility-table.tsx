import type { Facility } from "@/services/facilities-api";
import { Edit } from "lucide-react";
import { Badge } from "@/components/ui/badge";

interface FacilityTableProps {
  facilities: Facility[];
  onEdit: (facility: Facility) => void;
}

export const FacilityTable = ({ facilities, onEdit }: FacilityTableProps) => {
  return (
    <div className="w-full overflow-auto">
      <table className="w-full text-left border-collapse min-w-full">
        <thead>
          <tr className="border-b border-outline-variant bg-surface">
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">
              Facility Code
            </th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">
              Name
            </th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">
              License Number
            </th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">
              Target State
            </th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">
              Phone Number
            </th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider text-right bg-surface-container-low font-bold">
              Actions
            </th>
          </tr>
        </thead>
        <tbody className="divide-y divide-outline-variant text-body-sm text-on-surface font-body-sm">
          {facilities.length === 0 ? (
            <tr>
              <td colSpan={6} className="p-[12px_16px] text-center text-on-surface-variant">
                No facilities found.
              </td>
            </tr>
          ) : (
            facilities.map((facility, index) => (
              <tr
                key={facility.id}
                className={`hover:bg-primary/5 transition-colors group border-b border-outline-variant/30 ${
                  index % 2 !== 0 ? "bg-surface-container-lowest" : "bg-surface"
                }`}
              >
                <td className="p-[16px_16px] font-mono-data text-mono-data text-on-surface-variant font-medium">
                  {facility.facility_code}
                </td>
                <td className="p-[16px_16px] font-medium text-on-surface">{facility.name}</td>
                <td className="p-[16px_16px] font-mono-data text-mono-data text-on-surface-variant">
                  {facility.license_number}
                </td>
                <td className="p-[16px_16px]">
                  <Badge variant="outline" className="border-secondary/30 bg-secondary/100 text-secondary-foreground hover:bg-secondary/20">
                    {facility.target_state}
                  </Badge>
                </td>
                <td className="p-[16px_16px] font-mono-data text-mono-data text-on-surface-variant">
                  {facility.phone_number}
                </td>
                <td className="p-[16px_16px] text-right">
                  <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                    <button
                      onClick={() => onEdit(facility)}
                      className="p-2 text-on-surface-variant hover:text-primary hover:bg-primary/10 rounded-md transition-colors"
                      title="Edit Facility"
                    >
                      <Edit className="w-[18px] h-[18px]" />
                    </button>
                  </div>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};
