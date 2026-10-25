import { useState } from "react";
import { toast } from "sonner";
import { useRooms } from "../hooks/useRooms";
import { useFacilities } from "../hooks/useFacilities";
import { useDebounce } from "@/hooks/useDebounce";
import type { Room } from "@/services/rooms-api";
import { FacilitySelector } from "../components/rooms-bed-tab/facility-selector";
import { RoomToolbar } from "../components/rooms-bed-tab/room-toolbar";
import { RoomsTable } from "../components/rooms-bed-tab/rooms-table";
import { Pagination } from "../components/rooms-bed-tab/pagination";
import { FormModal } from "@/components/common/form-modal";
import { RoomForm } from "../components/rooms-bed-tab/room-form";
import { BedForm } from "../components/rooms-bed-tab/bed-form";
import type { Bed } from "@/services/rooms-api";

export const RoomsBedTab = () => {
  const [selectedFacility, setSelectedFacility] = useState<number | undefined>(undefined);
  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 300);
  
  const { rooms, metadata, page, pageSize, setPage, setPageSize, isLoading: isRoomsLoading, addRoom, editRoom, addBed, editBed } = useRooms(selectedFacility, debouncedSearchTerm);
  const { facilities, isLoading: isFacilitiesLoading } = useFacilities();
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRoom, setEditingRoom] = useState<Room | undefined>(undefined);

  const [isBedModalOpen, setIsBedModalOpen] = useState(false);
  const [editingBed, setEditingBed] = useState<Bed | undefined>(undefined);
  const [editingBedRoomId, setEditingBedRoomId] = useState<number | undefined>(undefined);
  
  const [isAddBedMode, setIsAddBedMode] = useState(false);

  const handleAddClick = () => {
    setEditingRoom(undefined);
    setIsModalOpen(true);
  };

  const handleEditClick = (room: Room) => {
    setEditingRoom(room);
    setIsModalOpen(true);
  };

  const handleAddBedClick = (roomId: number) => {
    setEditingBedRoomId(roomId);
    setEditingBed(undefined);
    setIsAddBedMode(true);
    setIsBedModalOpen(true);
  };

  const handleEditBedClick = (roomId: number, bed: Bed) => {
    setEditingBedRoomId(roomId);
    setEditingBed(bed);
    setIsAddBedMode(false);
    setIsBedModalOpen(true);
  };

  const handleSubmit = async (data: any) => {
    try {
      if (editingRoom) {
        await editRoom(editingRoom.id, data);
        toast.success("Room updated successfully");
      } else {
        await addRoom(data);
        toast.success("Room created successfully");
      }
      setIsModalOpen(false);
    } catch (error: any) {
      toast.error(error.message || "An error occurred");
    }
  };

  const handleEditBedSubmit = async (data: Partial<Bed>) => {
    if (!editingBedRoomId) return;

    try {
      if (isAddBedMode) {
        await addBed(editingBedRoomId, data as any);
        toast.success("Bed created successfully");
      } else if (editingBed) {
        await editBed(editingBedRoomId, editingBed.id, data);
        toast.success("Bed updated successfully");
      }
      setIsBedModalOpen(false);
    } catch (error: any) {
      toast.error(error.message || "An error occurred");
    }
  };

  if (isRoomsLoading || isFacilitiesLoading) {
    return <div className="p-8 text-center text-on-surface-variant">Loading rooms data...</div>;
  }

  const totalRooms = metadata?.totalElements || 0;

  return (
    <div className="flex-1 flex flex-col h-full">
      <FacilitySelector 
        facilities={facilities} 
        selectedFacility={selectedFacility === undefined ? "all" : String(selectedFacility)} 
        onFacilityChange={(val) => setSelectedFacility(val === "all" ? undefined : Number(val))} 
      />

      <div className="bg-surface-container-lowest rounded-xl border border-outline-variant shadow-sm overflow-hidden flex flex-col mb-6">
        <RoomToolbar 
          onSearch={setSearchTerm} 
          onAddRoom={handleAddClick} 
          totalRooms={totalRooms} 
        />
        <RoomsTable rooms={rooms} onEdit={handleEditClick} onAddBed={handleAddBedClick} onEditBed={handleEditBedClick} />
        <Pagination 
          metadata={metadata} 
          page={page} 
          pageSize={pageSize} 
          onPageChange={setPage} 
          onPageSizeChange={setPageSize} 
        />
      </div>

      <FormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingRoom ? "Edit Room" : "Add New Room"}
      >
        <RoomForm
          initialData={editingRoom}
          facilities={facilities}
          onSubmit={handleSubmit}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>

      <FormModal
        isOpen={isBedModalOpen}
        onClose={() => setIsBedModalOpen(false)}
        title={isAddBedMode ? "Add Bed" : "Edit Bed"}
      >
        <BedForm
          initialData={editingBed}
          onSubmit={handleEditBedSubmit}
          onCancel={() => setIsBedModalOpen(false)}
        />
      </FormModal>
    </div>
  );
};