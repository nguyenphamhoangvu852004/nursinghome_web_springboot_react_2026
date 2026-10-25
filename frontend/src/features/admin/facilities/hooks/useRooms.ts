import { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { roomsApi } from "@/services/rooms-api";
import type { Room, Bed } from "@/services/rooms-api";

export function useRooms(facilityId?: number, searchTerm?: string) {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Reset to first page when search changes
  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['rooms', facilityId, page, pageSize, searchTerm],
    queryFn: () => {
      if (facilityId) {
        return roomsApi.getRooms(facilityId, page, pageSize, searchTerm);
      }
      return roomsApi.getAllRooms(page, pageSize, searchTerm);
    },
    enabled: true,
  });

  const createRoomMutation = useMutation({
    mutationFn: (room: Omit<Room, "id" | "facility_name" | "capacity" | "status" | "beds">) => 
      roomsApi.createRoom(facilityId!, room),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rooms', facilityId] });
    },
  });

  const updateRoomMutation = useMutation({
    mutationFn: ({ id, updates }: { id: number; updates: Partial<Room> }) => 
      roomsApi.updateRoom(facilityId!, id, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rooms', facilityId] });
    },
  });

  const createBedMutation = useMutation({
    mutationFn: ({ roomId, bedData }: { roomId: number, bedData: Omit<Bed, "id" | "room_id" | "resident_name" | "admission_date"> }) => 
      roomsApi.createBed(facilityId!, roomId, bedData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rooms', facilityId] });
    },
  });

  const updateBedMutation = useMutation({
    mutationFn: ({ roomId, bedId, updates }: { roomId: number, bedId: number, updates: Partial<Bed> }) => 
      roomsApi.updateBedStatus(facilityId!, roomId, bedId, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['rooms', facilityId] });
    },
  });

  return { 
    rooms: data?.data || [], 
    metadata: data?.metadata || null, 
    page, 
    pageSize, 
    setPage, 
    setPageSize, 
    isLoading, 
    error: error?.message || null, 
    fetchRooms: refetch, 
    addRoom: createRoomMutation.mutateAsync, 
    editRoom: (id: number, updates: Partial<Room>) => updateRoomMutation.mutateAsync({ id, updates }), 
    addBed: (roomId: number, bedData: Omit<Bed, "id" | "room_id" | "resident_name" | "admission_date">) => createBedMutation.mutateAsync({ roomId, bedData }), 
    editBed: (roomId: number, bedId: number, updates: Partial<Bed>) => updateBedMutation.mutateAsync({ roomId, bedId, updates }) 
  };
}
