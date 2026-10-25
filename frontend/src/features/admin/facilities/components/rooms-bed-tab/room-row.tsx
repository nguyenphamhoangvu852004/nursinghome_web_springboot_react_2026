import { useState } from "react";
import type { Room, Bed } from "@/services/rooms-api";
import { BedRow } from "./bed-row";
import { Edit, ChevronDown, ChevronRight, Plus } from "lucide-react";

interface RoomRowProps {
  room: Room;
  onEdit: (room: Room) => void;
  onAddBed: (roomId: number) => void;
  onEditBed: (roomId: number, bed: Bed) => void;
  index: number;
}

export const RoomRow = ({ room, onEdit, onAddBed, onEditBed, index }: RoomRowProps) => {
  const [isExpanded, setIsExpanded] = useState(false);

  return (
    <>
      <tr className={`hover:bg-primary/5 transition-colors group cursor-pointer border-b border-outline-variant/30 ${isExpanded ? 'bg-surface-container-low/30' : ''} ${index % 2 !== 0 && !isExpanded ? 'bg-surface-container-lowest' : 'bg-surface'}`} onClick={() => setIsExpanded(!isExpanded)}>
        <td className="p-[16px_16px]">
          <div className="flex items-center gap-2">
            <button className="text-on-surface-variant hover:text-on-surface transition-colors">
              {isExpanded ? <ChevronDown className="w-[20px] h-[20px]" /> : <ChevronRight className="w-[20px] h-[20px]" />}
            </button>
            <span className="font-medium text-on-surface">{room.room_number}</span>
          </div>
        </td>
        <td className="p-[16px_16px] text-on-surface-variant">{room.room_type}</td>
        <td className="p-[16px_16px] text-on-surface-variant">{room.facility_name}</td>
        <td className="p-[16px_16px]">
          <div className="flex items-center gap-2">
            <div className="flex -space-x-1">
              {[...Array(room.capacity)].map((_, i) => (
                <div key={i} className={`w-4 h-4 rounded-full border border-surface ${i < room.beds.filter(b => b.status === 'OCCUPIED').length ? 'bg-error' : 'bg-surface-container-highest'}`} title={i < room.beds.filter(b => b.status === 'OCCUPIED').length ? "Occupied" : "Available"}></div>
              ))}
            </div>
            <span className="text-body-sm text-on-surface-variant">{room.capacity} Beds</span>
          </div>
        </td>
        <td className="p-[16px_16px]">
          {room.status === "Full" ? (
             <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-error/10 text-error font-medium border border-error/20">Full</span>
          ) : room.status === "Available" ? (
             <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-primary/10 text-primary font-medium border border-primary/20">Available</span>
          ) : (
             <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-tertiary/10 text-tertiary font-medium border border-tertiary/20">{room.status}</span>
          )}
        </td>
        <td className="p-[16px_16px] text-right" onClick={(e) => e.stopPropagation()}>
          <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
            <button onClick={() => onAddBed(room.id)} className="p-2 text-on-surface-variant hover:text-primary hover:bg-primary/10 rounded-md transition-colors tooltip-trigger" title="Add Bed">
              <Plus className="w-[18px] h-[18px]" />
            </button>
            <button onClick={() => onEdit(room)} className="p-2 text-on-surface-variant hover:text-primary hover:bg-primary/10 rounded-md transition-colors" title="Edit Room">
              <Edit className="w-[18px] h-[18px]" />
            </button>
          </div>
        </td>
      </tr>
      {isExpanded && (
        <tr>
          <td colSpan={6} className="p-0 border-b border-outline-variant">
            <div className="bg-surface-container-low/20 px-4 py-3 border-l-4 border-l-primary/40">
              <table className="w-full text-left text-body-md">
                <thead>
                  <tr className="border-b border-outline-variant/50 text-on-surface-variant">
                    <th className="p-[12px_16px] pl-12 font-medium">Bed Identifier</th>
                    <th className="p-[12px_16px] font-medium">Status</th>
                    <th className="p-[12px_16px] font-medium">Current Resident</th>
                    <th className="p-[12px_16px] font-medium">Admission Date</th>
                    <th className="p-[12px_16px] font-medium text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-outline-variant/30">
                  {room.beds.map((bed, bedIndex) => (
                    <BedRow key={bed.id} bed={bed} onEdit={(bed) => onEditBed(room.id, bed)} index={bedIndex} />
                  ))}
                </tbody>
              </table>
            </div>
          </td>
        </tr>
      )}
    </>
  );
};
