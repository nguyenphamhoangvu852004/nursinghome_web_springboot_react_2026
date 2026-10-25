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
import type { PreResponse } from "../types/pre-admission-type";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";

const statusVariant = {
  DRAFT: "secondary",
  COMPLETED: "default",
  REJECTED: "destructive",
} as const;

interface Props {
  onDecide: (row: PreResponse, decision: "COMPLETED" | "REJECTED") => void;
  onDelete: (row: PreResponse) => void;
}

export const getScreeningColumns = ({
  onDecide,
  onDelete,
}: Props): ColumnDef<PreResponse>[] => {
  return [
    { accessorKey: "id", header: "ID" },
    {
      accessorKey: "residentName",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Resident" />
      ),
    },
    {
      accessorKey: "status",
      header: "Status",
      cell: ({ row }) => (
        <Badge variant={statusVariant[row.original.status]}>
          {row.original.status}
        </Badge>
      ),
    },
    {
      accessorKey: "createdAt",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Created At" />
      ),
      cell: ({ row }) => new Date(row.original.createdAt).toLocaleDateString(),
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => {
        const s = row.original;
        if (s.status !== "DRAFT") return null;
        return (
          <DropdownMenu>
            <DropdownMenuTrigger>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => onDecide(s, "COMPLETED")}>
                Approve
              </DropdownMenuItem>
              <DropdownMenuItem onClick={() => onDecide(s, "REJECTED")}>
                Reject
              </DropdownMenuItem>
              <DropdownMenuItem
                variant="destructive"
                onClick={() => onDelete(s)}
              >
                Delete
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        );
      },
    },
  ];
};
