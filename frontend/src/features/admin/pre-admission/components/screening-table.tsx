import type { PaginationState, SortingState } from "@tanstack/react-table";
import { getScreeningColumns } from "./columns";
import { useScreenings } from "../hooks/use-screenings";
import { useDecideScreening } from "../hooks/use-decide-screening";
import { DataTable } from "@/components/data-table/data-table";
import { useScreeningSearchParams } from "../search-params";
import { Button } from "@/components/ui/button";
import { useDialogStore } from "@/store/use-dialog-store";
import { useConfirmStore } from "@/store/use-confirm-store";
import { CreateScreeningForm } from "./create-screening-form";
import { Plus } from "lucide-react";
import type { PreResponse } from "../types/pre-admission-type";
import { useDeleteScreening } from "../hooks/use-delete-screening";

export const ScreeningTable = () => {
  const [{ page, size, sort }, setParams] = useScreeningSearchParams();
  const { data, isLoading } = useScreenings({ page, size, sort });
  const decideMutation = useDecideScreening();
  const deleteMutation = useDeleteScreening();
  const openDialog = useDialogStore((s) => s.open);
  const openConfirm = useConfirmStore((s) => s.open);

  const pagination: PaginationState = { pageIndex: page, pageSize: size };
  const sorting: SortingState = sort
    ? [{ id: sort.split(",")[0], desc: sort.split(",")[1] === "desc" }]
    : [];

  const handleDecide = (
    row: PreResponse,
    decision: "COMPLETED" | "REJECTED",
  ) => {
    openConfirm({
      title:
        decision === "COMPLETED" ? "Approve screening?" : "Reject screening?",
      description: `Confirm ${
        decision === "COMPLETED" ? "approving" : "rejecting"
      } the screening for ${row.residentName}.`,
      onConfirm: () =>
        decideMutation.mutate({ id: row.id, payload: { status: decision } }),
    });
  };

  const handleDelete = (row: PreResponse) => {
    openConfirm({
      title: "Delete screening?",
      description: `This will permanently delete the draft screening for ${row.residentName}.`,
      onConfirm: () => deleteMutation.mutate(row.id),
    });
  };

  const columns = getScreeningColumns({
    onDecide: handleDecide,
    onDelete: handleDelete,
  });

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button
          onClick={() =>
            openDialog({
              title: "Create Screening",
              content: <CreateScreeningForm />,
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
              : "createdAt,desc",
          })
        }
      />
    </div>
  );
};
