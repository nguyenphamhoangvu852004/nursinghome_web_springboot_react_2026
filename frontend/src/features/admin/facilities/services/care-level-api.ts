import { apiClient } from '@/lib/api-client';
import type {
  CareLevelRateResponse,
  CreateCareLevelRateRequest,
  UpdateCareLevelRateRequest,
  CareLevelResponse,
  UpdateCareLevelRequest,
} from '../types';

export const careLevelApi = {
  // Get all care levels
  getCareLevels: async () => {
    const { data } = await apiClient.get<CareLevelResponse[]>('/admin/care-levels');
    return data;
  },

  // Update care level (enable/disable)
  updateCareLevel: async (careLevelId: number, data: UpdateCareLevelRequest) => {
    const { data: responseData } = await apiClient.patch<CareLevelResponse>(`/admin/care-levels/${careLevelId}`, data);
    return responseData;
  },

  // Get care level rates by care level id (optional)
  getCareLevelRates: async (careLevelId?: number) => {
    const { data } = await apiClient.get<CareLevelRateResponse[]>('/admin/care-level-rates', {
      params: careLevelId ? { care_level_id: careLevelId } : {},
    });
    return data;
  },

  // Create new care level rate
  createCareLevelRate: async (data: CreateCareLevelRateRequest) => {
    const { data: responseData } = await apiClient.post<CareLevelRateResponse>('/admin/care-level-rates', data);
    return responseData;
  },

  // Update care level rate
  updateCareLevelRate: async (rateId: number, data: UpdateCareLevelRateRequest) => {
    const { data: responseData } = await apiClient.put<CareLevelRateResponse>(`/admin/care-level-rates/${rateId}`, data);
    return responseData;
  },

  // Delete care level rate
  deleteCareLevelRate: async (rateId: number) => {
    await apiClient.delete(`/admin/care-level-rates/${rateId}`);
  },

  // Seed sample data
  seedSampleRates: async () => {
    const { data } = await apiClient.post<CareLevelRateResponse[]>('/admin/care-level-rates/seed');
    return data;
  },
};
