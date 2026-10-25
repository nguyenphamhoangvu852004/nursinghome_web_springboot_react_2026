import { axiosInstance } from "@/lib/axios";
import type {
    CareLevelHistory,
    LocClassificationResult,
} from "../types/loc.type";

export const residentLocService = {

    getHistory: async (
        residentId: number
    ): Promise<CareLevelHistory[]> => {

        const response = await axiosInstance.get(
            `/residents/${residentId}/care-level-history`
        );

        return response.data;
    },

    getClassificationResult: async (
        residentId: number
    ): Promise<LocClassificationResult> => {

        const response = await axiosInstance.get(
            `/assessments/resident/${residentId}/classification-result`
        );

        return response.data.data;
    }
};