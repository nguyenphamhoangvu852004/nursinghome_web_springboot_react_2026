import { useQuery } from "@tanstack/react-query";
import { getFacilityForSelect } from "../services/facility-service";

export const useFacilityForSelect = () => {
  return useQuery({
    queryKey: ["facilities", "select"],
    queryFn: getFacilityForSelect,
  });
};
