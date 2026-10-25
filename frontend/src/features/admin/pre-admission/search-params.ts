import { parseAsInteger, parseAsString, useQueryStates } from "nuqs";

export const screeningSearchParams = {
  page: parseAsInteger.withDefault(0),
  size: parseAsInteger.withDefault(10),
  sort: parseAsString.withDefault("createdAt,desc"),
};

export const useScreeningSearchParams = () => {
  return useQueryStates(screeningSearchParams);
};
