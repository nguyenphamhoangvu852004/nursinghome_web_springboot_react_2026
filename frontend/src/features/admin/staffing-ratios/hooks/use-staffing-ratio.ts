import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
    getStaffingRatio,
    updateStaffingRatio,
} from "../services/staffing-ratio.service";
import type { UpdateStaffingRatioPayload } from "../types/staffing-ratio.type";

export const useStaffingRatio = () => {
    return useQuery({
        queryKey: ["staffing-ratio"],
        queryFn: getStaffingRatio,
    });
};

export const useUpdateStaffingRatio = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: UpdateStaffingRatioPayload) =>
            updateStaffingRatio(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["staffing-ratio"] });
        },
    });
};