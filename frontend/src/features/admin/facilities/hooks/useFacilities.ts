import { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { facilitiesApi } from "@/services/facilities-api";
import type { Facility } from "@/services/facilities-api";

export function useFacilities(searchTerm?: string) {
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  // Reset to first page when search changes
  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['facilities', page, pageSize, searchTerm],
    queryFn: () => facilitiesApi.getFacilities(page, pageSize, searchTerm),
  });

  const createFacilityMutation = useMutation({
    mutationFn: facilitiesApi.createFacility,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facilities'] });
    },
  });

  const updateFacilityMutation = useMutation({
    mutationFn: ({ id, updates }: { id: number; updates: Partial<Facility> }) => 
      facilitiesApi.updateFacility(id, updates),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['facilities'] });
    },
  });

  return { 
    facilities: data?.data || [], 
    metadata: data?.metadata || null, 
    page, 
    pageSize, 
    setPage, 
    setPageSize, 
    isLoading, 
    error: error?.message || null, 
    fetchFacilities: refetch, 
    addFacility: createFacilityMutation.mutateAsync, 
    editFacility: (id: number, updates: Partial<Facility>) => updateFacilityMutation.mutateAsync({ id, updates }) 
  };
}
