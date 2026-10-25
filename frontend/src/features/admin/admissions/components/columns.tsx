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
import type { AdmissionResponse } from "../types/admission-type";
import { DataTableColumnHeader } from "@/components/data-table/data-table-column-header";

export const getAdmissionColumns = (
  onDischarge: (row: AdmissionResponse) => void,
): ColumnDef<AdmissionResponse>[] => {
  return [
    { accessorKey: "id", header: "ID" },
    { accessorKey: "residentName", header: "Resident" },
    { accessorKey: "facilityId", header: "Facility ID" },
    {
      accessorKey: "admissionDate",
      header: ({ column }) => (
        <DataTableColumnHeader column={column} title="Admission Date" />
      ),
    },
    {
      accessorKey: "status",
      header: "Status",
      cell: ({ row }) => (
        <Badge
          variant={row.original.status === "ACTIVE" ? "default" : "secondary"}
        >
          {row.original.status}
        </Badge>
      ),
    },
    {
      id: "actions",
      header: "Actions",
      cell: ({ row }) => {
        const a = row.original;
        if (a.status !== "ACTIVE") return null;
        return (
          <DropdownMenu>
            <DropdownMenuTrigger>
              <Button variant="ghost" className="h-8 w-8 p-0">
                <MoreHorizontal className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => onDischarge(a)}>
                Discharge
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        );
      },
    },
  ];
};
