import { apiClient } from "@/lib/api-client";

import type { ApiResponse, PagedApiResponse } from "@/types/api";

export type SupplyStatus = "OK" | "LOW_STOCK" | "OUT_OF_STOCK";

export interface SupplyCategoryResponse {
    category_id: number;
    category_name: string;
}

export interface SupplyFacilityResponse {
    facility_id: number;
    facility_name: string;
}

export interface SupplyResponse {
    id: number;
    item_name: string;
    category: SupplyCategoryResponse;
    facility: SupplyFacilityResponse;
    stock_on_hand: number;
    total: number;
    reorder_threshold: string;
    unit_cost: string;
    private_pay_rate: string;
    status: SupplyStatus;
}

export interface SupplyCreateRequest {
    item_name: string;
    category_id: number;
    facility_id: number;
    stock_on_hand: number;
    reorder_threshold: string;
    unit_cost: string;
    private_pay_rate: string;
}

export interface SupplyUpdateRequest {
    item_name: string;
    category_id: number;
    facility_id: number;
    reorder_threshold: string;
    unit_cost: string;
    private_pay_rate: string;
}

export interface supplyListParams {
    page?: number;
    size?: number;
}

const BASE_URL = "/admin/inventory/supplies";


export const supplyApi = {
    getSupplies: async (params: supplyListParams = {},): Promise<PagedApiResponse<SupplyResponse[]>> => {
        const { data } = await apiClient.get<PagedApiResponse<SupplyResponse[]>>(BASE_URL, { params });

        return data;

    },

    createSupply: async (payLoad: SupplyCreateRequest): Promise<ApiResponse<SupplyResponse>> => {
        const { data } = await apiClient.post<ApiResponse<SupplyResponse>>(BASE_URL, payLoad);

        return data;
    },

    getReorderThreshold: async (params: supplyListParams = {})
        : Promise<PagedApiResponse<SupplyResponse[]>> => {
        const { data } = await apiClient.get<PagedApiResponse<SupplyResponse[]>>(`${BASE_URL}/low-stock`, { params });

        return data;
    },

    getSupplyById: async (supplyId: number): Promise<ApiResponse<SupplyResponse>> => {
        const { data } = await apiClient.get<ApiResponse<SupplyResponse>>(`${BASE_URL}/${supplyId}`);

        return data;
    },

    updateSupply: async (supplyId: number, payLoad: SupplyUpdateRequest): Promise<ApiResponse<SupplyResponse>> => {
        const { data } = await apiClient.put<ApiResponse<SupplyResponse>>(`${BASE_URL}/${supplyId}`, payLoad);

        return data;
    },

    deleteSupply: async (supplyId: number): Promise<ApiResponse<SupplyResponse>> => {
        const { data } = await apiClient.delete<ApiResponse<SupplyResponse>>(`${BASE_URL}/${supplyId}`);

        return data;
    },

}