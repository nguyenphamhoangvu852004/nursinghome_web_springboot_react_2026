import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Plus, Search } from "lucide-react";

interface RoomToolbarProps {
  onSearch: (value: string) => void;
  onAddRoom: () => void;
  totalRooms: number;
}

export const RoomToolbar = ({ onSearch, onAddRoom, totalRooms }: RoomToolbarProps) => {
  return (
    <div className="px-6 py-4 border-b border-outline-variant flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-surface-container-low/30">
      <div className="flex items-center gap-2">
        <h3 className="text-headline-md font-headline-md text-on-surface">Rooms Overview</h3>
        <span className="bg-surface-container-high text-on-surface-variant text-label-md px-2 py-0.5 rounded-full">
          {totalRooms} Total
        </span>
      </div>
      <div className="flex items-center gap-3 w-full sm:w-auto">
        <div className="relative flex-1 sm:w-64">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-on-surface-variant w-[18px] h-[18px]" />
          <Input
            className="pl-9 pr-4 py-1.5 bg-surface text-body-sm border border-outline-variant rounded-lg focus:ring-1 focus:ring-primary focus:border-primary w-full placeholder:text-on-surface-variant/70"
            placeholder="Search rooms..."
            onChange={(e) => onSearch(e.target.value)}
          />
        </div>
        <Button onClick={onAddRoom} className="bg-primary !text-white hover:bg-primary/90 px-4 py-2 rounded-lg text-label-md font-label-md shadow-sm transition-colors flex items-center gap-2">
          <Plus className="w-[18px] h-[18px]" />
          Add Room
        </Button>
      </div>
    </div>
  );
};
