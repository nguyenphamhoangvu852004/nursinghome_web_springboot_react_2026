import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";
import { deleteAssessment } from "../services/assessment-service";

export const useDeleteAssessment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteAssessment,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["assessments"] });
      toast.success(data.message ?? "Deleted successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
