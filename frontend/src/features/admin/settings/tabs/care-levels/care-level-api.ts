import axios from 'axios';
import type {
  CareLevelRateResponse,
  CreateCareLevelRateRequest,
  UpdateCareLevelRateRequest,
  CareLevelResponse,
  UpdateCareLevelRequest,
} from '../../../facilities/types';
import { API_BASE_URL } from '../../../facilities/utils/constants';

export const careLevelApi = {
  // Get all care levels
  getCareLevels: () =>
    axios.get<CareLevelResponse[]>(`${API_BASE_URL}/admin/care-levels`),

  // Update care level (enable/disable)
  updateCareLevel: (careLevelId: number, data: UpdateCareLevelRequest) =>
    axios.patch<CareLevelResponse>(`${API_BASE_URL}/admin/care-levels/${careLevelId}`, data),

  /// Get care level rates by care level id
getCareLevelRates: (careLevelId: number) => {
  const token = localStorage.getItem("token");
  console.log("TOKEN:", token);

  return axios.get<CareLevelRateResponse[]>(
    `${API_BASE_URL}/admin/care-level-rates`,
    {
      params: { care_level_id: careLevelId },
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );
},

  // Create new care level rate
  createCareLevelRate: (data: CreateCareLevelRateRequest) =>
    axios.post<CareLevelRateResponse>(`${API_BASE_URL}/admin/care-level-rates`, data),

  // Update care level rate
  updateCareLevelRate: (rateId: number, data: UpdateCareLevelRateRequest) =>
    axios.put<CareLevelRateResponse>(`${API_BASE_URL}/admin/care-level-rates/${rateId}`, data),

  // Delete care level rate
  deleteCareLevelRate: (rateId: number) =>
    axios.delete(`${API_BASE_URL}/admin/care-level-rates/${rateId}`),

  // Seed sample data
  seedSampleRates: () =>
    axios.post<CareLevelRateResponse[]>(`${API_BASE_URL}/admin/care-level-rates/seed`),
};
