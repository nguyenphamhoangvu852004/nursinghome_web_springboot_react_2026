import { useQuery } from "@tanstack/react-query";
import {
  getScreenings,
  type ScreeningListParams,
} from "../services/pre-admission-service";

export const useScreenings = (params: ScreeningListParams) =>
  useQuery({
    queryKey: ["pre-admissions", "list", params],
    queryFn: () => getScreenings(params),
  });
