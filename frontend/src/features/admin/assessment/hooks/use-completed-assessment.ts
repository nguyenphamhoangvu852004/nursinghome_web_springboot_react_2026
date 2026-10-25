import { useQuery } from "@tanstack/react-query";
import { getCompletedForSelect } from "../services/assessment-service";

export const useCompletedAssessmentsForSelect = () =>
  useQuery({
    queryKey: ["assessments", "completed"],
    queryFn: getCompletedForSelect,
  });
