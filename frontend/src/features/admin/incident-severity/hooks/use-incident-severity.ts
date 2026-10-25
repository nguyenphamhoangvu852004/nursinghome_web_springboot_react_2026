import { useQuery } from "@tanstack/react-query";
import { fetchIncidentSeverityLevels } from "../services/incident-severity-service";

export const useIncidentSeverityLevels = () => {
  return useQuery({
    queryKey: ["incidentSeverityLevels"],
    queryFn: fetchIncidentSeverityLevels,
    staleTime: 1000 * 60 * 5,
  });
};
