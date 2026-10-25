import { parseAsInteger, parseAsString, useQueryStates } from "nuqs";

export const assessmentSearchParams = {
  page: parseAsInteger.withDefault(0),
  size: parseAsInteger.withDefault(10),
  sort: parseAsString.withDefault("id,desc"),
};

export const useAssessmentSearchParams = () => {
  return useQueryStates(assessmentSearchParams);
};
