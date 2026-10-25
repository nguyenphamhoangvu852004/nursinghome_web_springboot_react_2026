import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";
import { decideAssessment } from "../services/assessment-service";
import type { AssessmentDecisionRequest } from "../types/assessment-type";

export const useDecideAssessment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number;
      payload: AssessmentDecisionRequest;
    }) => decideAssessment(id, payload),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["assessments"] });
      toast.success(data.message ?? "Updated successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
