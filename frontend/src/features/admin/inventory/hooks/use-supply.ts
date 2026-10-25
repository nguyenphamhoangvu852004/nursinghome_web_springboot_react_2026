import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { inventoryCategoryApi, type InventoryCategoryResponse } from "../api/inventory-category-api";
import { supplyApi, type SupplyCreateRequest, type SupplyResponse, type SupplyUpdateRequest } from "../api/supply-api";
import { facilitiesApi, type Facility } from "@/services/facilities-api";
import type { SupplyItem } from "../store/use-supply-store";

const supplyQueryKey = ["inventory-supply"] as const;
const supplyOptionsQueryKey = ["inventory-supply-options"] as const;

const mapSupplyResponse = (supply: SupplyResponse): SupplyItem => {
  // Thực hiện console.log ở đây
  console.log('Mapping supply:', supply);

  return {
    id: supply.id,
    itemName: supply.item_name,
    category: {
      id: supply.category.category_id,
      name: supply.category.category_name,
    },
    facility: {
      id: supply.facility.facility_id,
      name: supply.facility.facility_name,
    },
    stockOnHand: supply.stock_on_hand,
    total: supply.total,
    reorderThreshold: supply.reorder_threshold,
    unitCost: supply.unit_cost,
    privatePayRate: supply.private_pay_rate,
    status: supply.status,
  };
};


const mapSupplyList = (supply: SupplyResponse[] | undefined): SupplyItem[] => {
    if (!Array.isArray(supply)) {
        return [];
    }

    return supply.map(mapSupplyResponse);
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

export function useSupply(selectedSupplyId?: number | null) {
    const queryClient = useQueryClient();

    const supplyQuery = useQuery({
        queryKey: supplyQueryKey,
        queryFn: async () => {
            const response = await supplyApi.getSupplies({ page: 0, size: 100 });
            return mapSupplyList(response.data);
        },
    });

    const supplyDetailQuery = useQuery({
        queryKey: [...supplyQueryKey, selectedSupplyId],
        enabled: selectedSupplyId != null,
        queryFn: async () => {
            const response = await supplyApi.getSupplyById(selectedSupplyId as number);
            return mapSupplyResponse(response.data);
        },
    });

    const categoriesQuery = useQuery({
        queryKey: [...supplyOptionsQueryKey, "categories"],
        queryFn: async () => {
            const response = await inventoryCategoryApi.getInventoryCategories({ page: 0, size: 100 });
            return mapCategoryOptions(response.data);
        },
    });

    const facilitiesQuery = useQuery({
        queryKey: [...supplyOptionsQueryKey, "facilities"],
        queryFn: async () => {
            const response = await facilitiesApi.getFacilities(0, 100);
            return mapFacilityOptions(response.data);
        },
    });

    const invalidateSupplyQueries = async () => {
        await Promise.all([
            queryClient.invalidateQueries({ queryKey: supplyQueryKey }),
            queryClient.invalidateQueries({ queryKey: [...supplyQueryKey, selectedSupplyId] }),
        ]);
    };

    const createSupplyMutation = useMutation({
        mutationFn: (payload: SupplyCreateRequest) => supplyApi.createSupply(payload),
        onSuccess: invalidateSupplyQueries,
    });

    const updateSupplyMutation = useMutation({
        mutationFn: ({ supplyId, payload }: { supplyId: number, payload: SupplyUpdateRequest }) => supplyApi.updateSupply(supplyId, payload),
        onSuccess: invalidateSupplyQueries,
    });

    const deleteSupplyMutation = useMutation({
        mutationFn: (supplyId: number) => supplyApi.deleteSupply(supplyId),
        onSuccess: invalidateSupplyQueries,
    });

    return {
        supplies: supplyQuery.data ?? [],
        isLoadingSupplies: supplyQuery.isLoading,
        suppliesError: supplyQuery.error instanceof Error ? supplyQuery.error.message : null,
        refetchSupplies: supplyQuery.refetch,

        selectedSupply: supplyDetailQuery.data ?? null,
        isLoadingSelectedSupply: supplyDetailQuery.isLoading,
        selectedSupplyError: supplyDetailQuery.error instanceof Error ? supplyDetailQuery.error.message : null,

        categoriesOptions: categoriesQuery.data ?? [],
        isLoadingCategoriesOptions: categoriesQuery.isLoading,

        facilitiesOptions: facilitiesQuery.data ?? [],
        isLoadingFacilitiesOptions: facilitiesQuery.isLoading,

        createSupply: createSupplyMutation.mutateAsync,
        isCreatingSupply: createSupplyMutation.isPending,

        updateSupply: updateSupplyMutation.mutateAsync,
        isUpdatingSupply: updateSupplyMutation.isPending,

        deleteSupply: deleteSupplyMutation.mutateAsync,
        isDeletingSupply: deleteSupplyMutation.isPending,
    };
}
