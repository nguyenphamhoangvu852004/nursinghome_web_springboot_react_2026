import { useQuery } from "@tanstack/react-query";
import { fetchSLAConfigs } from "../services/sla-config-service";

export const useSLAConfigs = () => {
  return useQuery({
    queryKey: ["slaConfigs"],
    queryFn: fetchSLAConfigs,
    staleTime: 1000 * 60 * 5,
  });
};
