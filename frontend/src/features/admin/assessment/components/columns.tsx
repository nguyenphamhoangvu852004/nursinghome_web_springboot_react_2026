import type { ColumnDef } from "@tanstack/react-table";
import { MoreHorizontal } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import type { AssessmentResponse } from "../types/assessment-type";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";

interface Props {
  onDecide: (row: AssessmentResponse) => void;
  onUpdate: (row: AssessmentResponse) => void;
  onViewDetail: (row: AssessmentResponse) => void;
  onDelete: (row: AssessmentResponse) => void;
}

export const getAssessmentColumns = ({
  onDecide,
  onUpdate,
  onViewDetail,
  onDelete,
}: Props): ColumnDef<AssessmentResponse>[] => {
  return [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "residentName", header: "Resident" },
    {
      accessorKey: "adlTotalScore",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="ADL Score" />
      ),
    },
    {
      accessorKey: "status",
      header: "Status",
      cell: ({ row }) => (
        <Badge
          variant={
            row.original.status === "COMPLETED" ? "default" : "secondary"
          }
        >
          {row.original.status}
        </Badge>
      ),
    },
    {
      id: "detailsCount",
      header: "Metrics Recorded",
      cell: ({ row }) => row.original.details?.length ?? 0,
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => {
        const a = row.original;
        return (
          <DropdownMenu>
            <DropdownMenuTrigger>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => onViewDetail(a)}>
                View Detail
              </DropdownMenuItem>
              {a.status === "DRAFT" && (
                <>
                  <DropdownMenuItem onClick={() => onUpdate(a)}>
                    Update
                  </DropdownMenuItem>
                  <DropdownMenuItem onClick={() => onDecide(a)}>
                    Decide
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    variant="destructive"
                    onClick={() => onDelete(a)}
                  >
                    Delete
                  </DropdownMenuItem>
                </>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
        );
      },
    },
  ];
};
