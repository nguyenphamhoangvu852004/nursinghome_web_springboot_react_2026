import { apiClient } from "@/lib/api-client";
import type { ApiResponse, PagedApiResponse } from "@/types/api";

export interface InventoryCategoryRequest {
	category_name: string;
	description: string;
}

export interface InventoryCategoryResponse {
	id: number;
	category_name: string;
	description: string;
	created_at: string;
}

export interface InventoryCategoryListParams {
	page?: number;
	size?: number;
}

const BASE_URL = "/admin/inventory/categories";

export const inventoryCategoryApi = {
	getInventoryCategories: async (
		params: InventoryCategoryListParams = {},
	): Promise<PagedApiResponse<InventoryCategoryResponse[]>> => {
		const { data } = await apiClient.get<PagedApiResponse<InventoryCategoryResponse[]>>(
			BASE_URL,
			{ params },
		);

		return data;
	},

	getInventoryCategoryById: async (
		categoryId: number,
	): Promise<ApiResponse<InventoryCategoryResponse>> => {
		const { data } = await apiClient.get<ApiResponse<InventoryCategoryResponse>>(
			`${BASE_URL}/${categoryId}`,
		);

		return data;
	},

	createInventoryCategory: async (
		payload: InventoryCategoryRequest,
	): Promise<ApiResponse<InventoryCategoryResponse>> => {
		const { data } = await apiClient.post<ApiResponse<InventoryCategoryResponse>>(
			BASE_URL,
			payload,
		);

		return data;
	},

	updateInventoryCategory: async (
		categoryId: number,
		payload: InventoryCategoryRequest,
	): Promise<ApiResponse<InventoryCategoryResponse>> => {
		const { data } = await apiClient.put<ApiResponse<InventoryCategoryResponse>>(
			`${BASE_URL}/${categoryId}`,
			payload,
		);

		return data;
	},

	deleteInventoryCategory: async (categoryId: number): Promise<void> => {
		await apiClient.delete(`${BASE_URL}/${categoryId}`);
	},
};
