import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import type { AxiosError } from "axios";
import type { ApiResponse } from "@/types/api";
import type { AdmissionDischargeRequest } from "../types/admission-type";
import { dischargeAdmission } from "../services/admission-service";

export const useDischargeAdmission = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      payload,
    }: {
      id: number;
      payload: AdmissionDischargeRequest;
    }) => dischargeAdmission(id, payload),
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["admissions"] });
      toast.success(data.message ?? "Discharged successfully");
    },
    onError: (error: AxiosError<ApiResponse<null>>) => {
      toast.error(error.response?.data?.message ?? "Something went wrong");
    },
  });
};
