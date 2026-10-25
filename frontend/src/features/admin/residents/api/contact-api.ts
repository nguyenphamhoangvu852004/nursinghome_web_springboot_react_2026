import { apiClient } from "@/lib/api-client";

import type {
    ContactDetail,
    ContactListContainer,
    CreateContactRequest,
    ResidentByContact,
    UpdateContactRequest,
} from "../types/contact.types";

interface ApiResponse<T> {
    statusCode: number;
    message: string;
    data: T | null;
}

export interface GetContactsParams {
    search?: string;
    includeDeleted?: boolean;
    page?: number;
    pageSize?: number;
}

export const contactApi = {
    getContacts: async (params: GetContactsParams): Promise<ContactListContainer> => {
        const currentPage = params.page ?? 1;
        const currentPageSize = params.pageSize ?? 10;

        const response = await apiClient.get<ApiResponse<ContactListContainer>>("/contacts", {
            params: {
                search: params.search?.trim() || undefined,
                include_deleted: params.includeDeleted ?? false,
                page: currentPage,
                page_size: currentPageSize,
            },
        });

        const data = response.data?.data;

        if (!data) {
            return {
                contacts: [],
                meta: {
                    total: 0,
                    page: currentPage,
                    pageSize: currentPageSize,
                    totalPages: 0,
                    hasNext: false,
                    hasPrevious: false,
                },
            };
        }

        return {
            contacts: Array.isArray(data.contacts) ? data.contacts : [],
            meta: {
                total: data.meta?.total ?? 0,
                page: data.meta?.page ?? currentPage,
                pageSize: data.meta?.pageSize ?? currentPageSize,
                totalPages: data.meta?.totalPages ?? 0,
                hasNext: data.meta?.hasNext ?? false,
                hasPrevious: data.meta?.hasPrevious ?? false,
            },
        };
    },

    getContactById: async (id: number): Promise<ContactDetail> => {
        const response = await apiClient.get<ApiResponse<{ contact: ContactDetail }>>(`/contacts/${id}`);
        const contact = response.data?.data?.contact;

        if (!contact) {
            throw new Error(response.data?.message || "Contact data was not returned.");
        }

        return contact;
    },

    createContact: async (payload: CreateContactRequest): Promise<ContactDetail> => {
        const response = await apiClient.post<ApiResponse<{ contact: ContactDetail }>>("/contacts", payload);
        const contact = response.data?.data?.contact;

        if (!contact) {
            throw new Error(response.data?.message || "Created contact data was not returned.");
        }

        return contact;
    },

    updateContact: async (
        id: number,
        payload: UpdateContactRequest,
    ): Promise<ContactDetail | null> => {
        const response = await apiClient.patch<ApiResponse<{ contact: ContactDetail }>>(
            `/contacts/${id}`,
            payload,
        );

        return response.data?.data?.contact ?? null;
    },

    deleteContact: async (id: number): Promise<void> => {
        await apiClient.delete(`/contacts/${id}`);
    },

    getResidentsByContact: async (id: number): Promise<ResidentByContact[]> => {
        const response = await apiClient.get<ApiResponse<ResidentByContact[]>>(
            `/contacts/${id}/residents`,
        );

        return Array.isArray(response.data?.data) ? response.data.data : [];
    },
};