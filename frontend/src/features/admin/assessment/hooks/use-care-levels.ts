import { useQuery } from "@tanstack/react-query";
import { getCareLevels } from "../services/care-level";

export const useCareLevels = () =>
  useQuery({
    queryKey: ["care-levels"],
    queryFn: getCareLevels,
  });
