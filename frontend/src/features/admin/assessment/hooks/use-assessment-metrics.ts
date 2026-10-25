import { useQuery } from "@tanstack/react-query";
import { getMetrics } from "../services/assessment-service";

export const useAssessmentMetrics = () =>
  useQuery({
    queryKey: ["assessments", "metrics"],
    queryFn: getMetrics,
  });
