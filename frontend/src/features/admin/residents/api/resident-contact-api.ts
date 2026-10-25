import { API_BASE_URL } from "@/common/config/api-url";
import axios from "axios";
import { apiClient } from "@/lib/api-client";

import type {
    CreateResidentContactRequest,
    ResidentContact,
    ResidentContactApiResponse,
    ResidentQuickContact,
    UpdateResidentContactRequest,
} from "../types/resident-contact.types";

interface ApiResponse<T> {
    statusCode: number;
    message: string;
    data: T | null;
}

const residentContactClient = axios.create({
    baseURL: API_BASE_URL,
    headers: { "Content-Type": "application/json" },
});

residentContactClient.interceptors.request.use(
    (config) => {
        const token =
            localStorage.getItem("accessToken") ??
            localStorage.getItem("access_token") ??
            localStorage.getItem("token");

        if (token) config.headers.Authorization = `Bearer ${token}`;
        return config;
    },
    (error) => Promise.reject(error),
);

const normalizeResidentContact = (item: ResidentContactApiResponse): ResidentContact => ({
    residentContactId: item.residentContactId,
    residentId: item.residentId,
    contactId: item.contactId,
    firstName: item.firstName,
    middleName: item.middleName ?? null,
    lastName: item.lastName,
    fullName: item.fullName,
    phonePrimary: item.phonePrimary,
    phoneSecondary: item.phoneSecondary ?? null,
    email: item.email ?? null,
    addressId: item.addressId ?? null,
    relationshipType: item.relationshipType,
    isPrimary: item.isPrimary ?? item.primary ?? false,
    isEmergencyContact: item.isEmergencyContact ?? item.emergencyContact ?? false,
    isGuarantor: item.isGuarantor ?? item.guarantor ?? false,
    financialResponsibilityPct: Number(item.financialResponsibilityPct) || 0,
    createdAt: item.createdAt,
});

const extractErrorMessage = (error: unknown): string => {
    if (axios.isAxiosError(error)) {
        const responseData = error.response?.data;

        if (typeof responseData === "string") return responseData;

        if (responseData && typeof responseData === "object") {
            const data = responseData as { message?: string; error?: string };

            return (
                data.message ??
                data.error ??
                `Request failed with status ${error.response?.status}`
            );
        }

        return error.message;
    }

    if (error instanceof Error) return error.message;
    return "An unexpected error occurred.";
};

export const residentContactApi = {
    // API 34
    async getResidentContacts(residentId: number): Promise<ResidentContact[]> {
        try {
            const response = await residentContactClient.get<ResidentContactApiResponse[]>(
                `/residents/${residentId}/contacts`,
            );

            return (Array.isArray(response.data) ? response.data : []).map(normalizeResidentContact);
        } catch (error) {
            throw new Error(extractErrorMessage(error));
        }
    },

    // API 35
    async createResidentContact(
        residentId: number,
        payload: CreateResidentContactRequest,
    ): Promise<ResidentContact> {
        try {
            const response = await residentContactClient.post<ResidentContactApiResponse>(
                `/residents/${residentId}/contacts`,
                payload,
            );

            return normalizeResidentContact(response.data);
        } catch (error) {
            throw new Error(extractErrorMessage(error));
        }
    },

    // API 36
    async updateResidentContact(
        residentId: number,
        residentContactId: number,
        payload: UpdateResidentContactRequest,
    ): Promise<ResidentContact> {
        try {
            const response = await residentContactClient.patch<ResidentContactApiResponse>(
                `/residents/${residentId}/contacts/${residentContactId}`,
                payload,
            );

            return normalizeResidentContact(response.data);
        } catch (error) {
            throw new Error(extractErrorMessage(error));
        }
    },

    // API 37
    async deleteResidentContact(residentId: number, residentContactId: number): Promise<void> {
        try {
            await residentContactClient.delete(
                `/residents/${residentId}/contacts/${residentContactId}`,
            );
        } catch (error) {
            throw new Error(extractErrorMessage(error));
        }
    },

    getResidentGuarantorContact: async (residentId: number): Promise<ResidentQuickContact> => {
        const response = await apiClient.get<ApiResponse<{ contact: ResidentQuickContact }>>(
            `/residents/${residentId}/contacts/guarantor`,
        );

        const contact = response.data.data?.contact;

        if (!contact) {
            throw new Error(
                response.data.message || "Guarantor contact was not returned.",
            );
        }

        return contact;
    },

    getResidentPrimaryContact: async (residentId: number): Promise<ResidentQuickContact> => {
        const response = await apiClient.get<ApiResponse<{ contact: ResidentQuickContact }>>(
            `/residents/${residentId}/contacts/primary`,
        );

        const contact = response.data.data?.contact;

        if (!contact) {
            throw new Error(
                response.data.message || "Primary contact was not returned.",
            );
        }

        return contact;
    },
};