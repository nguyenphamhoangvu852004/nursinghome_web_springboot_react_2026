import { useQuery } from "@tanstack/react-query";
import { getPendingResidents } from "../services/resident-service";

export const usePendingResidents = () =>
  useQuery({
    queryKey: ["residents", "pending"],
    queryFn: getPendingResidents,
  });
