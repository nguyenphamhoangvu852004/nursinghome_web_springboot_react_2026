import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { deleteScreening } from "../services/pre-admission-service";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";

export const useDeleteScreening = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteScreening,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["pre-admissions"] });
      toast.success(data.message ?? "Deleted successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
