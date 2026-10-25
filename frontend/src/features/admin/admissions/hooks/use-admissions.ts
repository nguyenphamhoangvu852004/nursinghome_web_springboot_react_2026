import { useQuery } from "@tanstack/react-query";
import {
  getAdmissions,
  type AdmissionListParams,
} from "../services/admission-service";

export const useAdmissions = (params: AdmissionListParams) =>
  useQuery({
    queryKey: ["admissions", "list", params],
    queryFn: () => getAdmissions(params),
  });
