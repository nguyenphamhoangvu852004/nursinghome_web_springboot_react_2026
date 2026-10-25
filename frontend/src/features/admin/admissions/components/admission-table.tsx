import type { PaginationState, SortingState } from "@tanstack/react-table";
import { useAdmissions } from "../hooks/use-admissions";
import { useDischargeAdmission } from "../hooks/use-discharge-admission";
import { useAdmissionSearchParams } from "../search-params";
import { getAdmissionColumns } from "./columns";
import { DataTable } from "@/components/data-table/data-table";
import { Button } from "@/components/ui/button";
import { useDialogStore } from "@/store/use-dialog-store";
import { useConfirmStore } from "@/store/use-confirm-store";
import { CreateAdmissionForm } from "./create-admission-form";
import { Plus } from "lucide-react";

export const AdmissionTable = () => {
  const [{ page, size, sort }, setParams] = useAdmissionSearchParams();
  const { data, isLoading } = useAdmissions({ page, size, sort });
  const dischargeMutation = useDischargeAdmission();
  const openDialog = useDialogStore((s) => s.open);
  const openConfirm = useConfirmStore((s) => s.open);

  const pagination: PaginationState = { pageIndex: page, pageSize: size };
  const sorting: SortingState = sort
    ? [{ id: sort.split(",")[0], desc: sort.split(",")[1] === "desc" }]
    : [];

  const columns = getAdmissionColumns((row) =>
    openConfirm({
      title: "Discharge resident?",
      description: `Confirm discharging ${row.residentName}.`,
      onConfirm: () =>
        dischargeMutation.mutate({
          id: row.id,
          payload: {
            dischargeDate: new Date().toISOString().split("T")[0],
            dischargeReason: "",
          },
        }),
    }),
  );

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button
          onClick={() =>
            openDialog({
              title: "Create Admission",
              content: <CreateAdmissionForm />,
            })
          }
          size="lg"
        >
          <Plus /> Create
        </Button>
      </div>
      <DataTable
        columns={columns}
        data={data?.data ?? []}
        rowCount={data?.metadata.totalElements ?? 0}
        isLoading={isLoading}
        pagination={pagination}
        onPaginationChange={(p) =>
          setParams({ page: p.pageIndex, size: p.pageSize })
        }
        sorting={sorting}
        onSortingChange={(s) =>
          setParams({
            sort: s[0]
              ? `${s[0].id},${s[0].desc ? "desc" : "asc"}`
              : "admissionDate,desc",
          })
        }
      />
    </div>
  );
};
