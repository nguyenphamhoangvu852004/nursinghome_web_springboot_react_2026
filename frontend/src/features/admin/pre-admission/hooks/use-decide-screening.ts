import { useMutation, useQueryClient } from "@tanstack/react-query";
import { decideScreening } from "../services/pre-admission-service";
import type { PreDecisionRequest } from "../types/pre-admission-type";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";

export const useDecideScreening = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number;
      payload: PreDecisionRequest;
    }) => decideScreening(id, payload),

    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["pre-admissions"] });
      toast.success(data.message ?? "Updated successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
