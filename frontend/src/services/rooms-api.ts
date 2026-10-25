import type { PaginationMetadata } from "@/types/api";
import { apiClient } from "@/lib/api-client";

export type RoomType = "PRIVATE" | "SEMI_PRIVATE" | "WARD";
export type BedStatus = "AVAILABLE" | "OCCUPIED" | "MAINTENANCE";

export interface Bed {
  id: number;
  bed_number: string;
  status: BedStatus;
  room_id: number;
  resident_name: string | null;
  admission_date: string | null;
}

export interface Room {
  id: number;
  room_number: string;
  room_type: RoomType;
  facility_id: number;
  facility_name: string;
  capacity: number;
  status: string;
  beds: Bed[];
}

export const roomsApi = {
  getAllRooms: async (page: number = 0, size: number = 10, search?: string): Promise<{ data: Room[], metadata: PaginationMetadata }> => {
    const { data } = await apiClient.get(`/admin/facility-settings/rooms`, {
      params: { page, size, search: search?.trim() || undefined }
    });
    return { data: data.data, metadata: data.metadata };
  },
  getRooms: async (facilityId: number, page: number = 0, size: number = 10, search?: string): Promise<{ data: Room[], metadata: PaginationMetadata }> => {
    const { data } = await apiClient.get(`/admin/facility-settings/${facilityId}/rooms`, {
      params: { page, size, search: search?.trim() || undefined }
    });
    return { data: data.data, metadata: data.metadata };
  },
  createRoom: async (facilityId: number, room: Omit<Room, "id" | "facility_name" | "capacity" | "status" | "beds">): Promise<Room> => {
    const { data } = await apiClient.post(`/admin/facility-settings/${facilityId}/rooms`, room);
    return data.data || data;
  },
  createBed: async (facilityId: number, roomId: number, bed: Omit<Bed, "id" | "room_id" | "resident_name" | "admission_date">): Promise<Bed> => {
    const { data } = await apiClient.post(`/admin/facility-settings/${facilityId}/rooms/${roomId}/beds`, bed);
    return data.data || data;
  },
  updateRoom: async (facilityId: number, roomId: number, updates: Partial<Room>): Promise<Room> => {
    const { data } = await apiClient.put(`/admin/facility-settings/${facilityId}/rooms/${roomId}`, updates);
    return data.data || data;
  },
  updateBedStatus: async (facilityId: number, roomId: number, bedId: number, updates: Partial<Bed>): Promise<Bed> => {
    const { data } = await apiClient.put(`/admin/facility-settings/${facilityId}/rooms/${roomId}/beds/${bedId}`, updates);
    return data.data || data;
  },
};
