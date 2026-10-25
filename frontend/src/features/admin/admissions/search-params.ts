import { parseAsInteger, parseAsString, useQueryStates } from "nuqs";

export const admissionSearchParams = {
  page: parseAsInteger.withDefault(0),
  size: parseAsInteger.withDefault(10),
  sort: parseAsString.withDefault("admissionDate,desc"),
};

export const useAdmissionSearchParams = () => {
  return useQueryStates(admissionSearchParams);
};
