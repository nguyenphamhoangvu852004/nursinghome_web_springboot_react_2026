import type { PaginationMetadata } from "@/types/api";
import { apiClient } from "@/lib/api-client";

export interface Facility {
  id: number;
  facility_code: string;
  name: string;
  license_number: string;
  target_state: string;
  phone_number: string;
}

export const facilitiesApi = {
  getFacilities: async (page: number = 0, size: number = 10, search?: string): Promise<{ data: Facility[], metadata: PaginationMetadata }> => {
    const { data } = await apiClient.get('/admin/facility-settings', {
      params: { page, size, search: search?.trim() || undefined }
    });
    return { data: data.data, metadata: data.metadata };
  },
  createFacility: async (facility: Omit<Facility, "id">): Promise<Facility> => {
    const { data } = await apiClient.post('/admin/facility-settings', facility);
    return data.data || data;
  },
  updateFacility: async (id: number, updates: Partial<Facility>): Promise<Facility> => {
    const { data } = await apiClient.put(`/admin/facility-settings/${id}`, updates);
    return data.data || data;
  },
};
