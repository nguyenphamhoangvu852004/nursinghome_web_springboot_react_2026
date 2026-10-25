import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { inventoryCategoryApi, type InventoryCategoryResponse } from "../api/inventory-category-api";
import { equipmentApi, type EquipmentCreateRequest, type EquipmentResponse, type EquipmentStatusRequest, type EquipmentUpdateRequest } from "../api/equipment-api";
import { facilitiesApi, type Facility } from "@/services/facilities-api";
import type { EquipmentItem } from "../store/use-equipment-store";

const equipmentQueryKey = ["inventory-equipment"] as const;
const equipmentOptionsQueryKey = ["inventory-equipment-options"] as const;

const mapEquipmentResponse = (equipment: EquipmentResponse): EquipmentItem => ({
  id: equipment.id,
  itemName: equipment.item_name,
  category: {
    id: equipment.category.category_id,
    name: equipment.category.category_name,
  },
  assetTag: equipment.asset_tag,
  status: equipment.status,
  facility: {
    id: equipment.facility.facility_id,
    name: equipment.facility.facility_name,
  },
  unitValue: equipment.unit_value,
});

const mapEquipmentList = (equipment: EquipmentResponse[] | undefined): EquipmentItem[] => {
  if (!Array.isArray(equipment)) {
    return [];
  }

  return equipment.map(mapEquipmentResponse);
};

const mapCategoryOptions = (categories: InventoryCategoryResponse[] | undefined) => {
  if (!Array.isArray(categories)) {
    return [];
  }

  return categories.map((category) => ({
    id: category.id,
    name: category.category_name,
  }));
};

const mapFacilityOptions = (facilities: Facility[] | undefined) => {
  if (!Array.isArray(facilities)) {
    return [];
  }

  return facilities.map((facility) => ({
    id: facility.id,
    name: facility.name,
  }));
};

export function useEquipment(selectedEquipmentId?: number | null) {
  const queryClient = useQueryClient();

  const equipmentQuery = useQuery({
    queryKey: equipmentQueryKey,
    queryFn: async () => {
      const response = await equipmentApi.getEquipment({ page: 0, size: 100 });
      return mapEquipmentList(response.data);
    },
  });

  const equipmentDetailQuery = useQuery({
    queryKey: [...equipmentQueryKey, selectedEquipmentId],
    enabled: selectedEquipmentId != null,
    queryFn: async () => {
      const response = await equipmentApi.getEquipmentById(selectedEquipmentId as number);
      return mapEquipmentResponse(response.data);
    },
  });

  const categoriesQuery = useQuery({
    queryKey: [...equipmentOptionsQueryKey, "categories"],
    queryFn: async () => {
      const response = await inventoryCategoryApi.getInventoryCategories({ page: 0, size: 100 });
      return mapCategoryOptions(response.data);
    },
  });

  const facilitiesQuery = useQuery({
    queryKey: [...equipmentOptionsQueryKey, "facilities"],
    queryFn: async () => {
      const response = await facilitiesApi.getFacilities(0, 100);
      return mapFacilityOptions(response.data);
    },
  });

  const invalidateEquipmentQueries = async () => {
    await Promise.all([
      queryClient.invalidateQueries({ queryKey: equipmentQueryKey }),
      queryClient.invalidateQueries({ queryKey: [...equipmentQueryKey, selectedEquipmentId] }),
    ]);
  };

  const createEquipmentMutation = useMutation({
    mutationFn: (payload: EquipmentCreateRequest) => equipmentApi.createEquipment(payload),
    onSuccess: invalidateEquipmentQueries,
  });

  const updateEquipmentMutation = useMutation({
    mutationFn: ({ equipmentId, payload }: { equipmentId: number; payload: EquipmentUpdateRequest }) =>
      equipmentApi.updateEquipment(equipmentId, payload),
    onSuccess: invalidateEquipmentQueries,
  });

  const deleteEquipmentMutation = useMutation({
    mutationFn: (equipmentId: number) => equipmentApi.deleteEquipment(equipmentId),
    onSuccess: invalidateEquipmentQueries,
  });

  const updateStatusMutation = useMutation({
    mutationFn: ({ equipmentId, payload }: { equipmentId: number; payload: EquipmentStatusRequest }) =>
      equipmentApi.updateEquipmentStatus(equipmentId, payload),
    onSuccess: invalidateEquipmentQueries,
  });

  return {
    equipment: equipmentQuery.data ?? [],
    isLoadingEquipment: equipmentQuery.isLoading,
    equipmentError: equipmentQuery.error instanceof Error ? equipmentQuery.error.message : null,
    refetchEquipment: equipmentQuery.refetch,

    selectedEquipment: equipmentDetailQuery.data ?? null,
    isLoadingSelectedEquipment: equipmentDetailQuery.isLoading,
    selectedEquipmentError: equipmentDetailQuery.error instanceof Error ? equipmentDetailQuery.error.message : null,

    categoryOptions: categoriesQuery.data ?? [],
    isLoadingCategoryOptions: categoriesQuery.isLoading,

    facilityOptions: facilitiesQuery.data ?? [],
    isLoadingFacilityOptions: facilitiesQuery.isLoading,

    createEquipment: createEquipmentMutation.mutateAsync,
    isCreatingEquipment: createEquipmentMutation.isPending,

    updateEquipment: updateEquipmentMutation.mutateAsync,
    isUpdatingEquipment: updateEquipmentMutation.isPending,

    deleteEquipment: deleteEquipmentMutation.mutateAsync,
    isDeletingEquipment: deleteEquipmentMutation.isPending,

    updateEquipmentStatus: updateStatusMutation.mutateAsync,
    isUpdatingEquipmentStatus: updateStatusMutation.isPending,
  };
}