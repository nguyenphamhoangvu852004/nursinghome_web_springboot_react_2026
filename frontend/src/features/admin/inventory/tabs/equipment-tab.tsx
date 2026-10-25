import { useEffect, useMemo } from "react";

import type { EquipmentStatus } from "../api/equipment-api";
import { EquipmentDeleteDialog } from "../components/equipment-tab/equipment-delete-dialog";
import { EquipmentDetailView } from "../components/equipment-tab/equipment-detail-view";
import { EquipmentFormView } from "../components/equipment-tab/equipment-form-view";
import { EquipmentListView } from "../components/equipment-tab/equipment-list-view";
import { useEquipment } from "../hooks/use-equipment";
import { useEquipmentStore, type EquipmentFormValues } from "../store/use-equipment-store";

const mapCreatePayload = (values: EquipmentFormValues) => ({
  item_name: values.itemName,
  category_id: Number(values.categoryId),
  asset_tag: values.assetTag,
  facility_id: Number(values.facilityId),
  unit_value: values.unitValue,
});

const mapUpdatePayload = (values: EquipmentFormValues) => ({
  item_name: values.itemName,
  category_id: Number(values.categoryId),
  facility_id: Number(values.facilityId),
  unit_value: values.unitValue,
});

const statusOrder: Array<EquipmentStatus | "ALL"> = ["ALL", "AVAILABLE", "IN_SERVICE", "UNDER_MAINTENANCE", "RETIRED"];

export default function EquipmentTab() {
  const screen = useEquipmentStore((state) => state.screen);
  const selectedEquipmentId = useEquipmentStore((state) => state.selectedEquipmentId);
  const selectedEquipment = useEquipmentStore((state) => state.selectedEquipment);
  const formValues = useEquipmentStore((state) => state.formValues);
  const searchTerm = useEquipmentStore((state) => state.searchTerm);
  const statusFilter = useEquipmentStore((state) => state.statusFilter);
  const errorMessage = useEquipmentStore((state) => state.errorMessage);
  const deleteDialogOpen = useEquipmentStore((state) => state.deleteDialogOpen);
  const openList = useEquipmentStore((state) => state.openList);
  const openCreate = useEquipmentStore((state) => state.openCreate);
  const openDetail = useEquipmentStore((state) => state.openDetail);
  const openUpdate = useEquipmentStore((state) => state.openUpdate);
  const setErrorMessage = useEquipmentStore((state) => state.setErrorMessage);
  const setSearchTerm = useEquipmentStore((state) => state.setSearchTerm);
  const setStatusFilter = useEquipmentStore((state) => state.setStatusFilter);
  const setScreen = useEquipmentStore((state) => state.setScreen);
  const openDeleteDialog = useEquipmentStore((state) => state.openDeleteDialog);
  const closeDeleteDialog = useEquipmentStore((state) => state.closeDeleteDialog);
  const resetEquipmentState = useEquipmentStore((state) => state.resetEquipmentState);

  const {
    equipment,
    isLoadingEquipment,
    equipmentError,
    createEquipment,
    isCreatingEquipment,
    updateEquipment,
    isUpdatingEquipment,
    deleteEquipment,
    isDeletingEquipment,
    categoryOptions,
    isLoadingCategoryOptions,
    facilityOptions,
    isLoadingFacilityOptions,
  } = useEquipment(selectedEquipmentId);

  const isSubmitting = isCreatingEquipment || isUpdatingEquipment;
  const isDeleting = isDeletingEquipment;

  useEffect(() => {
    const nextError = equipmentError ?? null;
    setErrorMessage(nextError);
  }, [equipmentError, setErrorMessage]);

  const filteredEquipment = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLowerCase();

    return equipment.filter((item) => {
      const matchesSearch =
        normalizedSearch.length === 0 ||
        [item.itemName, item.assetTag, item.category.name, item.facility.name, item.status]
          .join(" ")
          .toLowerCase()
          .includes(normalizedSearch);

      const matchesStatus = statusFilter === "ALL" || item.status === statusFilter;

      return matchesSearch && matchesStatus;
    });
  }, [equipment, searchTerm, statusFilter]);

  const metrics = useMemo(
    () => ({
      total: equipment.length,
      available: equipment.filter((item) => item.status === "AVAILABLE").length,
      inService: equipment.filter((item) => item.status === "IN_SERVICE").length,
      underMaintenance: equipment.filter((item) => item.status === "UNDER_MAINTENANCE").length,
    }),
    [equipment],
  );

  const handleCreateSubmit = async (values: EquipmentFormValues) => {
    setErrorMessage(null);

    try {
      await createEquipment(mapCreatePayload(values));
      resetEquipmentState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to create equipment.");
    }
  };

  const handleUpdateSubmit = async (values: EquipmentFormValues) => {
    if (!selectedEquipment) {
      return;
    }

    setErrorMessage(null);

    try {
      await updateEquipment({ equipmentId: selectedEquipment.id, payload: mapUpdatePayload(values) });
      resetEquipmentState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to update equipment.");
    }
  };

  const handleDeleteConfirm = async () => {
    if (!selectedEquipment) {
      return;
    }

    setErrorMessage(null);

    try {
      await deleteEquipment(selectedEquipment.id);
      resetEquipmentState();
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Failed to delete equipment.");
    }
  };

  return (
    <div className="space-y-5 rounded-2xl border border-slate-200/70 bg-slate-50/70 p-4 shadow-sm sm:p-6">
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-1">
          <div className="text-sm font-semibold text-slate-500">Admin / Inventory</div>
          <h2 className="text-2xl font-bold tracking-tight text-slate-900">Equipment management</h2>
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
        <EquipmentListView
          equipment={filteredEquipment}
          isLoading={isLoadingEquipment}
          metrics={metrics}
          searchTerm={searchTerm}
          statusFilter={statusFilter}
          onAdd={openCreate}
          onSearchTermChange={setSearchTerm}
          onSelect={openDetail}
          onStatusFilterChange={setStatusFilter}
        />
      ) : null}

      {screen === "create" ? (
        <EquipmentFormView
          key="create-equipment-form"
          actionLabel="Save"
          cancelLabel="Cancel"
          categories={categoryOptions}
          facilities={facilityOptions}
          initialValues={formValues}
          isSubmitting={isSubmitting || isLoadingCategoryOptions || isLoadingFacilityOptions}
          mode="create"
          title="New Equipment"
          onCancel={openList}
          onSubmit={handleCreateSubmit}
        />
      ) : null}

      {screen === "detail" && selectedEquipment ? (
        <EquipmentDetailView
          equipment={selectedEquipment}
          onCancel={openList}
          onDelete={openDeleteDialog}
          onUpdate={openUpdate}
        />
      ) : null}

      {screen === "update" && selectedEquipment ? (
        <EquipmentFormView
          key={`update-equipment-${selectedEquipment.id}`}
          actionLabel="Save"
          cancelLabel="Cancel"
          categories={categoryOptions}
          facilities={facilityOptions}
          initialValues={formValues}
          isSubmitting={isSubmitting || isLoadingCategoryOptions || isLoadingFacilityOptions}
          mode="update"
          title="Update Equipment"
          onCancel={() => setScreen("detail")}
          onSubmit={handleUpdateSubmit}
        />
      ) : null}

      <EquipmentDeleteDialog
        equipmentName={selectedEquipment?.itemName ?? "this equipment"}
        isDeleting={isDeleting}
        open={deleteDialogOpen}
        onConfirm={handleDeleteConfirm}
        onOpenChange={(open) => (open ? openDeleteDialog() : closeDeleteDialog())}
      />
    </div>
  );
}