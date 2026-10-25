import type { Room, Bed } from "@/services/rooms-api";
import { RoomRow } from "./room-row";

interface RoomsTableProps {
  rooms: Room[];
  onEdit: (room: Room) => void;
  onAddBed: (roomId: number) => void;
  onEditBed: (roomId: number, bed: Bed) => void;
}

export const RoomsTable = ({ rooms, onEdit, onAddBed, onEditBed }: RoomsTableProps) => {
  return (
    <div className="overflow-x-auto">
      <table className="w-full text-left border-collapse min-w-[900px]">
        <thead>
          <tr className="border-b border-outline-variant bg-surface">
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider w-[200px] bg-surface-container-low font-bold">Room Number</th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">Room Type</th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">Facility</th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">Capacity</th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider bg-surface-container-low font-bold">Status</th>
            <th className="p-[16px_16px] text-label-bold font-label-bold text-on-surface uppercase tracking-wider text-right bg-surface-container-low font-bold">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-outline-variant text-body-sm text-on-surface font-body-sm">
          {rooms.length === 0 ? (
            <tr>
              <td colSpan={6} className="p-[12px_16px] text-center text-on-surface-variant">No rooms found.</td>
            </tr>
          ) : (
            rooms.map((room, index) => (
              <RoomRow key={room.id} room={room} onEdit={onEdit} onAddBed={onAddBed} onEditBed={onEditBed} index={index} />
            ))
          )}
        </tbody>
      </table>
    </div>
  );
};
