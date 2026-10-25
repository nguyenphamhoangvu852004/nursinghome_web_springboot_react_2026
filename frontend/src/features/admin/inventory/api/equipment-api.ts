import { apiClient } from "@/lib/api-client";
import type { ApiResponse, PagedApiResponse } from "@/types/api";

export type EquipmentStatus = "AVAILABLE" | "IN_SERVICE" | "UNDER_MAINTENANCE" | "RETIRED";

export interface EquipmentCategoryResponse {
    category_id: number;
    category_name: string;
}

export interface EquipmentFacilityResponse {
    facility_id: number;
    facility_name: string;
}

export interface EquipmentResponse {
    id: number;
    item_name: string;
    category: EquipmentCategoryResponse;
    asset_tag: string;
    status: EquipmentStatus;
    facility: EquipmentFacilityResponse;
    unit_value: string;
}

export interface EquipmentCreateRequest {
    item_name: string;
    category_id: number;
    asset_tag: string;
    facility_id: number;
    unit_value: string;
}

export interface EquipmentUpdateRequest {
    item_name: string;
    category_id: number;
    facility_id: number;
    unit_value: string;
}

export interface EquipmentStatusRequest {
    status: EquipmentStatus;
}

export interface EquipmentListParams {
    page?: number;
    size?: number;
}

const BASE_URL = "/admin/inventory/equipment";

export const equipmentApi = {
    getEquipment: async (
        params: EquipmentListParams = {},
    ): Promise<PagedApiResponse<EquipmentResponse[]>> => {
        const { data } = await apiClient.get<PagedApiResponse<EquipmentResponse[]>>(BASE_URL, {
            params,
        });

        return data;
    },

    getEquipmentById: async (equipmentId: number): Promise<ApiResponse<EquipmentResponse>> => {
        const { data } = await apiClient.get<ApiResponse<EquipmentResponse>>(`${BASE_URL}/${equipmentId}`);

        return data;
    },

    createEquipment: async (
        payload: EquipmentCreateRequest,
    ): Promise<ApiResponse<EquipmentResponse>> => {
        const { data } = await apiClient.post<ApiResponse<EquipmentResponse>>(BASE_URL, payload);

        return data;
    },

    updateEquipment: async (
        equipmentId: number,
        payload: EquipmentUpdateRequest,
    ): Promise<ApiResponse<EquipmentResponse>> => {
        const { data } = await apiClient.put<ApiResponse<EquipmentResponse>>(`${BASE_URL}/${equipmentId}`, payload);

        return data;
    },

    deleteEquipment: async (equipmentId: number): Promise<void> => {
        await apiClient.delete(`${BASE_URL}/${equipmentId}`);
    },

    updateEquipmentStatus: async (
        equipmentId: number,
        payload: EquipmentStatusRequest,
    ): Promise<ApiResponse<EquipmentResponse>> => {
        const { data } = await apiClient.patch<ApiResponse<EquipmentResponse>>(
            `${BASE_URL}/${equipmentId}/status`,
            payload,
        );

        return data;
    },
};

