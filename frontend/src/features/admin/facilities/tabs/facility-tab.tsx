import { useState } from "react";
import { toast } from "sonner";
import { useFacilities } from "../hooks/useFacilities";
import { useDebounce } from "@/hooks/useDebounce";
import type { Facility } from "@/services/facilities-api";
import { FacilityToolbar } from "../components/facilities-tab/facility-toolbar";
import { FacilityTable } from "../components/facilities-tab/facility-table";
import { FacilityPagination } from "../components/facilities-tab/facility-pagination";
import { FormModal } from "@/components/common/form-modal";
import { FacilityForm } from "../components/facilities-tab/facility-form";

export const FacilityTab = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const debouncedSearchTerm = useDebounce(searchTerm, 300);
  const { facilities, metadata, page, setPage, isLoading, addFacility, editFacility } = useFacilities(debouncedSearchTerm);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingFacility, setEditingFacility] = useState<Facility | undefined>(undefined);

  const handleAddClick = () => {
    setEditingFacility(undefined);
    setIsModalOpen(true);
  };

  const handleEditClick = (facility: Facility) => {
    setEditingFacility(facility);
    setIsModalOpen(true);
  };

  const handleSubmit = async (data: any) => {
    try {
      if (editingFacility) {
        await editFacility(editingFacility.id, data);
        toast.success("Facility updated successfully");
      } else {
        await addFacility(data);
        toast.success("Facility created successfully");
      }
      setIsModalOpen(false);
    } catch (error: any) {
      toast.error(error.message || "An error occurred");
    }
  };

  if (isLoading) {
    return <div className="p-8 text-center text-on-surface-variant">Loading facilities...</div>;
  }

  const activeCount = metadata?.totalElements || 0;

  return (
    <div className="flex-1 flex flex-col h-full">
      <FacilityToolbar
        onSearch={setSearchTerm}
        onAddFacility={handleAddClick}
        activeCount={activeCount}
      />
      
      <div className="bg-surface-container-lowest rounded-xl border border-outline-variant shadow-sm overflow-hidden flex flex-col mb-6 w-full">
        <FacilityTable facilities={facilities} onEdit={handleEditClick} />
        <FacilityPagination metadata={metadata} page={page} onPageChange={setPage} />
      </div>

      <FormModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={editingFacility ? "Edit Facility" : "Add New Facility"}
      >
        <FacilityForm
          initialData={editingFacility}
          onSubmit={handleSubmit}
          onCancel={() => setIsModalOpen(false)}
        />
      </FormModal>
    </div>
  );
};

