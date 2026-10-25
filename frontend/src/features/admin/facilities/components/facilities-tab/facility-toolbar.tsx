import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Search } from "lucide-react";

interface FacilityToolbarProps {
  onSearch: (value: string) => void;
  onAddFacility: () => void;
  activeCount: number;
}

export const FacilityToolbar = ({ onSearch, onAddFacility, activeCount }: FacilityToolbarProps) => {
  return (
    <>
      {/* Page Header & Global Tabs (Tabs are handled in FacilityPage, so just the title/search here conceptually, but matching HTML structure closely) */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 border-b border-outline-variant pb-px mb-6">
        <div>
          <h2 className="text-headline-xl font-headline-xl text-on-surface mb-4">Facility Settings</h2>
        </div>
        <div className="pb-3 flex items-center gap-3">
          <div className="relative hidden sm:block">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant w-[18px] h-[18px]" />
            <Input
              className="pl-9 pr-4 py-1.5 bg-surface text-body-sm border border-outline-variant rounded-lg focus:ring-1 focus:ring-primary focus:border-primary w-64 placeholder:text-on-surface-variant/70"
              placeholder="Search facilities..."
              onChange={(e) => onSearch(e.target.value)}
            />
          </div>
        <Button
          onClick={onAddFacility}
          className="bg-primary !text-white hover:bg-primary/90 px-4 py-2 rounded-lg text-label-md font-label-md shadow-sm transition-colors flex items-center gap-2"
        >
            <Plus className="w-[18px] h-[18px]" />
            Add Facility
          </Button>
        </div>
      </div>

      {/* Toolbar for the table container */}
      <div className="px-6 py-4 border-b border-outline-variant flex justify-between items-center bg-surface-container-low/30">
        <div className="flex items-center gap-2">
          <h3 className="text-headline-md font-headline-md text-on-surface">Registered Facilities</h3>
          <span className="bg-surface-container-high text-on-surface-variant text-label-md px-2 py-0.5 rounded-full">
            {activeCount} Total
          </span>
        </div>
      </div>
    </>
  );
};
