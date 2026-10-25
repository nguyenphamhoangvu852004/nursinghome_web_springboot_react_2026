import { useEffect } from "react";

import { type InventoryCategoryRequest } from "../api/inventory-category-api";
import { CategoryDeleteDialog } from "../components/category-tab/category-delete-dialog";
import { CategoryDetailView } from "../components/category-tab/category-detail-view";
import { CategoryFormView } from "../components/category-tab/category-form-view";
import { CategoryListView } from "../components/category-tab/category-list-view";

import { useInventoryCategories } from "../hooks/use-inventory-categories";
import { useInventoryCategoryStore, type CategoryFormValues, type CategoryItem } from "../store/use-inventory-category-store";

const mapCategoryRequest = (category: Pick<CategoryItem, "categoryName" | "description">): InventoryCategoryRequest => ({
  category_name: category.categoryName,
  description: category.description,
});

const emptyCategoryForm: CategoryFormValues = {
  categoryName: "",
  description: "",
};

export default function CategoryTab() {
  const screen = useInventoryCategoryStore((state) => state.screen);
  const selectedCategoryId = useInventoryCategoryStore((state) => state.selectedCategoryId);
  const selectedCategory = useInventoryCategoryStore((state) => state.selectedCategory);
  const formValues = useInventoryCategoryStore((state) => state.formValues);
  const errorMessage = useInventoryCategoryStore((state) => state.errorMessage);
  const deleteDialogOpen = useInventoryCategoryStore((state) => state.deleteDialogOpen);
  const openList = useInventoryCategoryStore((state) => state.openList);
  const openCreate = useInventoryCategoryStore((state) => state.openCreate);
  const openDetail = useInventoryCategoryStore((state) => state.openDetail);
  const openUpdate = useInventoryCategoryStore((state) => state.openUpdate);
  const setErrorMessage = useInventoryCategoryStore((state) => state.setErrorMessage);
  const setScreen = useInventoryCategoryStore((state) => state.setScreen);
  const openDeleteDialog = useInventoryCategoryStore((state) => state.openDeleteDialog);
  const closeDeleteDialog = useInventoryCategoryStore((state) => state.closeDeleteDialog);
  const resetCategoryState = useInventoryCategoryStore((state) => state.resetCategoryState);

  const {
    categories,
    isLoadingCategories,
    categoriesError,
    createCategory,
    isCreatingCategory,
    updateCategory,
    isUpdatingCategory,
    deleteCategory,
    isDeletingCategory,
  } = useInventoryCategories(selectedCategoryId);

  const isSubmitting = isCreatingCategory || isUpdatingCategory;
  const isDeleting = isDeletingCategory;

  useEffect(() => {
    const nextError = categoriesError ?? null;
    setErrorMessage(nextError);
  }, [categoriesError, setErrorMessage]);

  const handleCreateSubmit = async (values: typeof emptyCategoryForm) => {
    setErrorMessage(null);

    try {
      await createCategory(mapCategoryRequest(values));
      resetCategoryState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to create category.");
    }
  };

  const handleUpdateSubmit = async (values: typeof emptyCategoryForm) => {
    if (!selectedCategory) {
      return;
    }

    setErrorMessage(null);

    try {
      await updateCategory({ categoryId: selectedCategory.id, payload: mapCategoryRequest(values) });
      resetCategoryState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to update category.");
    }
  };

  const handleDeleteConfirm = async () => {
    if (!selectedCategory) {
      return;
    }

    setErrorMessage(null);

    try {
      await deleteCategory(selectedCategory.id);
      resetCategoryState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to delete category.");
    }
  };

  return (
    <div className="space-y-5 rounded-2xl border border-slate-200/70 bg-slate-50/70 p-4 shadow-sm sm:p-6">
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-1">
          <div className="text-sm font-semibold text-slate-500">Admin / Equipment &amp; Supply</div>
          <h2 className="text-2xl font-bold tracking-tight text-slate-900">Equipment &amp; Supply Inventory</h2>
          <p className="max-w-4xl text-sm text-slate-500">
            Durable Medical Equipment (DME) asset register and consumable supplies stock for the inventory categories module.
          </p>
        </div>
        <div className="rounded-full border border-slate-200 bg-white px-3 py-1 text-xs font-semibold text-slate-500 shadow-sm">
          Tab 2 of 2
        </div>
      </div>

      {errorMessage ? (
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">
          {errorMessage}
        </div>
      ) : null}

      {screen === "list" ? (
        <div className="space-y-5 rounded-2xl border border-slate-200 bg-white p-4 shadow-[0_1px_0_rgba(15,23,42,0.03)] sm:p-6">
          <div className="flex items-center justify-between gap-4">
            <div>
              <h3 className="text-lg font-semibold text-slate-800">Inventory categories</h3>
              <p className="text-sm text-slate-500">Select a category to view details, update, or delete it.</p>
            </div>
            <div className="hidden rounded-full border border-slate-200 bg-slate-50 px-3 py-1 text-xs font-semibold text-slate-500 sm:block">
              {isLoadingCategories ? "Loading..." : `${categories.length} categories`}
            </div>
          </div>

          <CategoryListView
            categories={categories}
            isLoading={isLoadingCategories}
            onAdd={openCreate}
            onSelect={openDetail}
          />
        </div>
      ) : null}

      {screen === "create" ? (
        <CategoryFormView
          key="create-category-form"
          title="New Category"
          actionLabel="Save"
          cancelLabel="Cancel"
          initialValues={formValues}
          isSubmitting={isSubmitting}
          onCancel={openList}
          onSubmit={handleCreateSubmit}
        />
      ) : null}

      {screen === "detail" && selectedCategory ? (
        <CategoryDetailView
          category={selectedCategory}
          onCancel={openList}
          onDelete={openDeleteDialog}
          onUpdate={openUpdate}
        />
      ) : null}

      {screen === "update" && selectedCategory ? (
        <CategoryFormView
          key={`update-category-${selectedCategory.id}`}
          title="Update Category"
          actionLabel="Save"
          cancelLabel="Cancel"
          initialValues={formValues}
          isSubmitting={isSubmitting}
          onCancel={() => setScreen("detail")}
          onSubmit={handleUpdateSubmit}
        />
      ) : null}

      <CategoryDeleteDialog
        categoryName={selectedCategory?.categoryName ?? "this category"}
        isDeleting={isDeleting}
        open={deleteDialogOpen}
        onConfirm={handleDeleteConfirm}
        onOpenChange={(open) => (open ? openDeleteDialog() : closeDeleteDialog())}
      />
    </div>
  );
}
