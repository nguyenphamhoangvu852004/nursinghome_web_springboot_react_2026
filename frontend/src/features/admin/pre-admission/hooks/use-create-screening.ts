import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createScreening } from "../services/pre-admission-service";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";

export const useCreateScreening = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createScreening,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["pre-admissions"] });
      toast.success(data.message ?? "Created successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
