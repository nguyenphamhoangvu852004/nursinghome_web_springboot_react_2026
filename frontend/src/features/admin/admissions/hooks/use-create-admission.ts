import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";
import { createAdmission } from "../services/admission-service";

export const useCreateAdmission = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createAdmission,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["admissions"] });
      toast.success(data.message ?? "Created successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
