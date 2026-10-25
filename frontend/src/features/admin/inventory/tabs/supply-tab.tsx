import { useEffect, useMemo } from "react";

import type { SupplyStatus } from "../api/supply-api";
import { SupplyDeleteDialog } from "../components/supply-tab/supply-delete-dialog";
import { SupplyDetailView } from "../components/supply-tab/supply-detail-view";
import { SupplyFormView } from "../components/supply-tab/supply-form-view";
import { SupplyListView } from "../components/supply-tab/supply-list-view";
import { useSupply } from "../hooks/use-supply";
import { useSupplyStore, type SupplyFormValues } from "../store/use-supply-store";

const mapCreatePayload = (values: SupplyFormValues) => ({
  item_name: values.itemName,
  category_id: Number(values.categoryId),
  facility_id: Number(values.facilityId),
  stock_on_hand: values.stockOnHand,
  reorder_threshold: values.reorderThreshold,
  unit_cost: values.unitCost,
  private_pay_rate: values.privatePayRate,
});

const mapUpdatePayload = (values: SupplyFormValues) => ({
  item_name: values.itemName,
  category_id: Number(values.categoryId),
  facility_id: Number(values.facilityId),
  reorder_threshold: values.reorderThreshold,
  unit_cost: values.unitCost,
  private_pay_rate: values.privatePayRate,
});

const statusOrder: Array<SupplyStatus | "ALL"> = ["ALL", "OK", "LOW_STOCK", "OUT_OF_STOCK"];


export default function SupplyTab() {
  const screen = useSupplyStore((state) => state.screen);
  const selectedSupplyId = useSupplyStore((state) => state.selectedSupplyId);
  const selectedSupply = useSupplyStore((state) => state.selectedSupply);
  const formValues = useSupplyStore((state) => state.formValues);
  const searchTerm = useSupplyStore((state) => state.searchTerm);
  const statusFilter = useSupplyStore((state) => state.statusFilter);
  const errorMessage = useSupplyStore((state) => state.errorMessage);
  const deleteDialogOpen = useSupplyStore((state) => state.deleteDialogOpen);
  const openList = useSupplyStore((state) => state.openList);
  const openCreate = useSupplyStore((state) => state.openCreate);
  const openDetail = useSupplyStore((state) => state.openDetail);
  const openUpdate = useSupplyStore((state) => state.openUpdate);
  const setErrorMessage = useSupplyStore((state) => state.setErrorMessage);
  const setSearchTerm = useSupplyStore((state) => state.setSearchTerm);
  const setStatusFilter = useSupplyStore((state) => state.setStatusFilter);
  const setScreen = useSupplyStore((state) => state.setScreen);
  const openDeleteDialog = useSupplyStore((state) => state.openDeleteDialog);
  const closeDeleteDialog = useSupplyStore((state) => state.closeDeleteDialog);
  const resetSupplyState = useSupplyStore((state) => state.resetSupplyState);

  const {
    supplies,
    isLoadingSupplies,
    suppliesError,
    categoriesOptions,
    isLoadingCategoriesOptions,
    facilitiesOptions,
    isLoadingFacilitiesOptions,
    createSupply,
    isCreatingSupply,
    updateSupply,
    isUpdatingSupply,
    deleteSupply,
    isDeletingSupply,

  } = useSupply(selectedSupplyId);

  const isSubmitting = isCreatingSupply || isUpdatingSupply;
  const isDeleting = isDeletingSupply;

  useEffect(() => {
    const nextError = suppliesError ?? null;
    setErrorMessage(nextError);
  }, [suppliesError, setErrorMessage]);

  const filteredSupplies = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();

    return supplies.filter((item) => {
      const matchesSearch =
        normalizedSearch.length === 0 ||
        [item.itemName, item.category.name, item.facility.name, item.status]
          .join(" ")
          .toLowerCase()
          .includes(normalizedSearch);

      const matchesStatus = statusFilter === "ALL" || item.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [supplies, searchTerm, statusFilter]);

  const metrics = useMemo(
    () => ({
      total: supplies.length,
      available: supplies.filter((item) => item.status === "OK").length,
      lowStock: supplies.filter((item) => item.status === "LOW_STOCK").length,
      outOfStock: supplies.filter((item) => item.status === "OUT_OF_STOCK").length,
    }),
    [supplies]
  );

  const handleCreateSubmit = async (values: SupplyFormValues) => {
    setErrorMessage(null);

    try {
      await createSupply(mapCreatePayload(values));
      resetSupplyState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to create supply.");
    }
  };

  const handleUpdateSubmit = async (values: SupplyFormValues) => {
    console.log("Debug - selectedSupply:", selectedSupply);
    if (!selectedSupply) {
      return;
    }

    setErrorMessage(null);

    try {
      await updateSupply({ supplyId: selectedSupply.id, payload: mapUpdatePayload(values) });
      resetSupplyState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to update supply.");
    }
  };

  const handleDeleteConfirm = async () => {
    if (!selectedSupply) {
      return;
    }

    setErrorMessage(null);

    try {
      await deleteSupply(selectedSupply.id);
      resetSupplyState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to delete supply.");
    }
  };

  return (
    <div className="space-y-5 rounded-2xl border border-slate-200/70 bg-slate-50/70 p-4 shadow-sm sm:p-6">
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-1">
          <div className="text-sm font-semibold text-slate-500">Admin / Inventory</div>
          <h2 className="text-2xl font-bold tracking-tight text-slate-900">Supply management</h2>
          <p className="max-w-4xl text-sm text-slate-500">
            Manage durable medical equipment assets, their categories, status, and facility assignment.
          </p>
        </div>
        <div className="rounded-full border border-slate-200 bg-white px-3 py-1 text-xs font-semibold text-slate-500 shadow-sm">
          {statusOrder.length - 1} statuses
        </div>
      </div>

      {errorMessage ? (
        <div className="rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-700">{errorMessage}</div>
      ) : null}

      {screen === "list" ? (
        <SupplyListView
          supplies={filteredSupplies}
          isLoading={isLoadingSupplies}
          searchTerm={searchTerm}
          statusFilter={statusFilter}
          metrics={metrics}
          onSearchTermChange={setSearchTerm}
          onStatusFilterChange={setStatusFilter}
          onAdd={openCreate}
          onSelect={openDetail}
        />
      ) : null}

      {screen === "create" ? (
        <SupplyFormView
          key="create-supply-form"
          title="Create Supply"
          actionLabel="Save"
          cancelLabel="Cancel"
          mode="create"
          initialValues={formValues}
          categories={categoriesOptions}
          facilities={facilitiesOptions}
          isSubmitting={isSubmitting || isLoadingCategoriesOptions || isLoadingFacilitiesOptions}
          onCancel={openList}
          onSubmit={handleCreateSubmit}
        />
      ) : null}

      {screen === "detail" && selectedSupply ? (
        <SupplyDetailView
          supply={selectedSupply}
          onCancel={openList}
          onDelete={openDeleteDialog}
          onUpdate={openUpdate}
        />
      ) : null}

      {screen === "update" && selectedSupply ? (
        <SupplyFormView
          key={`update-supply-${selectedSupply.id}`}
          title="Update Supply"
          actionLabel="Save"
          cancelLabel="Cancel"
          mode="update"
          initialValues={formValues}
          categories={categoriesOptions}
          facilities={facilitiesOptions}
          isSubmitting={isSubmitting || isLoadingCategoriesOptions || isLoadingFacilitiesOptions}
          onCancel={() => setScreen("detail")}
          onSubmit={handleUpdateSubmit}
        />
      ) : null}

      <SupplyDeleteDialog
        supplyName={selectedSupply?.itemName ?? "this supply"}
        isDeleting={isDeleting}
        open={deleteDialogOpen}
        onConfirm={handleDeleteConfirm}
        onOpenChange={(open) => (open ? openDeleteDialog() : closeDeleteDialog())}
      />
    </div>
  );
}