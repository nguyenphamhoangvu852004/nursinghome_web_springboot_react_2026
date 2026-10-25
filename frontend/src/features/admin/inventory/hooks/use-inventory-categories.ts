import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

import {
  inventoryCategoryApi,
  type InventoryCategoryRequest,
  type InventoryCategoryResponse,
} from "../api/inventory-category-api";
import type { CategoryItem } from "../store/use-inventory-category-store";

const categoriesQueryKey = ["inventory-categories"] as const;

const mapCategoryResponse = (category: InventoryCategoryResponse): CategoryItem => ({
  id: category.id,
  categoryName: category.category_name,
  description: category.description,
  createdAt: category.created_at,
});

const mapCategoryList = (categories: InventoryCategoryResponse[] | undefined): CategoryItem[] => {
  if (!Array.isArray(categories)) {
    return [];
  }

  return categories.map(mapCategoryResponse);
};

export function useInventoryCategories(selectedCategoryId?: number | null) {
  const queryClient = useQueryClient();

  const categoriesQuery = useQuery({
    queryKey: categoriesQueryKey,
    queryFn: async () => {
      const response = await inventoryCategoryApi.getInventoryCategories({ page: 0, size: 100 });
      return mapCategoryList(response.data);
    },
  });

  const categoryDetailQuery = useQuery({
    queryKey: [...categoriesQueryKey, selectedCategoryId],
    enabled: selectedCategoryId != null,
    queryFn: async () => {
      const response = await inventoryCategoryApi.getInventoryCategoryById(selectedCategoryId as number);
      return mapCategoryResponse(response.data);
    },
  });

  const invalidateCategoryQueries = async () => {
    await Promise.all([
      queryClient.invalidateQueries({ queryKey: categoriesQueryKey }),
      queryClient.invalidateQueries({ queryKey: [...categoriesQueryKey, selectedCategoryId] }),
    ]);
  };

  const createCategoryMutation = useMutation({
    mutationFn: (payload: InventoryCategoryRequest) => inventoryCategoryApi.createInventoryCategory(payload),
    onSuccess: invalidateCategoryQueries,
  });

  const updateCategoryMutation = useMutation({
    mutationFn: ({ categoryId, payload }: { categoryId: number; payload: InventoryCategoryRequest }) =>
      inventoryCategoryApi.updateInventoryCategory(categoryId, payload),
    onSuccess: invalidateCategoryQueries,
  });

  const deleteCategoryMutation = useMutation({
    mutationFn: (categoryId: number) => inventoryCategoryApi.deleteInventoryCategory(categoryId),
    onSuccess: invalidateCategoryQueries,
  });

  return {
    categories: categoriesQuery.data ?? [],
    isLoadingCategories: categoriesQuery.isLoading,
    categoriesError: categoriesQuery.error instanceof Error ? categoriesQuery.error.message : null,
    refetchCategories: categoriesQuery.refetch,

    selectedCategory: categoryDetailQuery.data ?? null,
    isLoadingSelectedCategory: categoryDetailQuery.isLoading,
    selectedCategoryError: categoryDetailQuery.error instanceof Error ? categoryDetailQuery.error.message : null,

    createCategory: createCategoryMutation.mutateAsync,
    isCreatingCategory: createCategoryMutation.isPending,

    updateCategory: updateCategoryMutation.mutateAsync,
    isUpdatingCategory: updateCategoryMutation.isPending,

    deleteCategory: deleteCategoryMutation.mutateAsync,
    isDeletingCategory: deleteCategoryMutation.isPending,
  };
}