import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";
import { updateAssessment } from "../services/assessment-service";

export const useUpdateAssessment = (id: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (payload: {
      details: { metricId: number; score: number; notes?: string }[];
    }) => updateAssessment(id, payload),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["assessments"] });
      toast.success(data.message ?? "Updated successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
