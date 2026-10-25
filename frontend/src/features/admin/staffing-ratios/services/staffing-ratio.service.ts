import apiClient from "@/common/config/api";
import type {
    StaffingRatioConfig,
    UpdateStaffingRatioPayload,
} from "../types/staffing-ratio.type";

interface StaffingRatioApiResponse {
    id: number;
    facility_id: number;
    min_hrs_per_resident_day: number;
    warn_below_percentage: number;
}

export const getStaffingRatio = async (): Promise<StaffingRatioConfig> => {
    const response = await apiClient.get<StaffingRatioApiResponse>(
        "/admin/staffing-ratio-config"
    );

    return {
        id: response.data.id,
        facilityId: response.data.facility_id,
        minHrsPerResidentDay: response.data.min_hrs_per_resident_day,
        warnBelowPercentage: response.data.warn_below_percentage,
    };
};

export const updateStaffingRatio = async (
    payload: UpdateStaffingRatioPayload
): Promise<StaffingRatioConfig> => {
    const response = await apiClient.put<StaffingRatioApiResponse>(
        "/admin/staffing-ratio-config",
        {
            min_hrs_per_resident_day: payload.minHrsPerResidentDay,
            warn_below_percentage: payload.warnBelowPercentage,
        }
    );

    return {
        id: response.data.id,
        facilityId: response.data.facility_id,
        minHrsPerResidentDay: response.data.min_hrs_per_resident_day,
        warnBelowPercentage: response.data.warn_below_percentage,
    };
};