import { useQuery } from "@tanstack/react-query";
import { getActiveAdmissions } from "../services/admission-service";

export const useActiveAdmissions = () =>
  useQuery({
    queryKey: ["admissions", "select-active"],
    queryFn: getActiveAdmissions,
  });
