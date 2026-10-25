import type { PaginationState, SortingState } from "@tanstack/react-table";
import { getAssessmentColumns } from "./columns";
import { DataTable } from "@/components/data-table/data-table";
import { useAssessmentSearchParams } from "../search-params";
import { Button } from "@/components/ui/button";
import { useDialogStore } from "@/store/use-dialog-store";
import { CreateAssessmentForm } from "./create-assessment-form";
import { DecideAssessmentForm } from "./decide-assessment-form";
import { useAssessments } from "../hooks/use-pending-assessment";
import { Plus } from "lucide-react";
import { UpdateAssessmentForm } from "./update-assessment-form";
import { AssessmentDetailView } from "./assessment-detail-view";
import { useConfirmStore } from "@/store/use-confirm-store";
import { useDeleteAssessment } from "../hooks/use-delete-assessment";

export const AssessmentTable = () => {
  const [{ page, size, sort }, setParams] = useAssessmentSearchParams();
  const { data, isLoading } = useAssessments({ page, size, sort });
  const deleteMutation = useDeleteAssessment();
  const openDialog = useDialogStore((s) => s.open);
  const openConfirm = useConfirmStore((s) => s.open);

  const pagination: PaginationState = { pageIndex: page, pageSize: size };
  const sorting: SortingState = sort
    ? [{ id: sort.split(",")[0], desc: sort.split(",")[1] === "desc" }]
    : [];

  const columns = getAssessmentColumns({
    onDecide: (row) =>
      openDialog({
        title: `Decide Assessment #${row.id}`,
        content: <DecideAssessmentForm row={row} />,
      }),
    onUpdate: (row) =>
      openDialog({
        title: `Update Assessment #${row.id}`,
        content: <UpdateAssessmentForm row={row} />,
      }),
    onViewDetail: (row) =>
      openDialog({
        title: `Assessment #${row.id} Detail`,
        content: <AssessmentDetailView row={row} />,
      }),
    onDelete: (row) =>
      openConfirm({
        title: "Delete assessment?",
        description: `This will permanently delete the draft assessment for ${row.residentName}.`,
        onConfirm: () => deleteMutation.mutate(row.id),
      }),
  });

  return (
    <div className="space-y-4">
      <div className="flex justify-end">
        <Button
          onClick={() =>
            openDialog({
              title: "Create Assessment",
              content: <CreateAssessmentForm />,
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
            sort: s[0] ? `${s[0].id},${s[0].desc ? "desc" : "asc"}` : "id,desc",
          })
        }
      />
    </div>
  );
};
