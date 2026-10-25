import { useQuery } from "@tanstack/react-query";
import { getCompletedScreenings } from "../services/pre-admission-service";

export const useCompletedScreenings = () =>
  useQuery({
    queryKey: ["pre-admissions", "completed"],
    queryFn: getCompletedScreenings,
  });
