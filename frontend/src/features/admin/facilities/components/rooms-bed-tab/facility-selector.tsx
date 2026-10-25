import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import type { Facility } from "@/services/facilities-api";
import { Building2 } from "lucide-react";

interface FacilitySelectorProps {
  facilities: Facility[];
  selectedFacility: string;
  onFacilityChange: (value: string) => void;
}

export const FacilitySelector = ({ facilities, selectedFacility, onFacilityChange }: FacilitySelectorProps) => {
  return (
    <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 border-b border-outline-variant pb-px mb-6">
      <div>
        <h2 className="text-headline-xl font-headline-xl text-on-surface mb-4 flex items-center gap-2">
          Rooms & Beds
        </h2>
      </div>
      <div className="pb-3 flex flex-col sm:flex-row items-start sm:items-center gap-3 w-full sm:w-auto">
        <label className="text-label-md text-on-surface-variant font-medium whitespace-nowrap hidden sm:block">
          Select Facility
        </label>
        <Select value={selectedFacility} onValueChange={(v) => onFacilityChange(v || "all")}>
          <SelectTrigger className="w-full sm:w-[280px] bg-surface border-outline-variant hover:bg-surface-container-low transition-colors">
            <div className="flex items-center gap-2">
              <Building2 className="w-[18px] h-[18px] text-on-surface-variant" />
              <SelectValue placeholder="All Facilities" />
            </div>
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">All Facilities</SelectItem>
            {facilities.map((f) => (
              <SelectItem key={f.id} value={f.id}>
                {f.name}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>
    </div>
  );
};
