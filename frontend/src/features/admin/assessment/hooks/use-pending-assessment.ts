import { useQuery } from "@tanstack/react-query";
import {
  getAssessments,
  type AssessmentListParams,
} from "../services/assessment-service";

export const useAssessments = (params: AssessmentListParams) =>
  useQuery({
    queryKey: ["assessments", "list", params],
    queryFn: () => getAssessments(params),
  });
