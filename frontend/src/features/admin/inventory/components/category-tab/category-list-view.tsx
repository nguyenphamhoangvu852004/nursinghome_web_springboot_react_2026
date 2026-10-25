import { Plus } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import type { CategoryItem } from "../../tabs/category-tab";

interface CategoryListViewProps {
  categories: CategoryItem[];
  isLoading: boolean;
  onAdd: () => void;
  onSelect: (category: CategoryItem) => void;
}

export function CategoryListView({ categories, isLoading, onAdd, onSelect }: CategoryListViewProps) {
  return (
    <div className="overflow-hidden rounded-xl border border-slate-200 bg-slate-50/40">
      <Table>
        <TableHeader className="bg-slate-50/80">
          <TableRow className="hover:bg-transparent">
            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Category name</TableHead>
            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Description</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {isLoading ? (
            <TableRow>
              <TableCell className="px-6 py-6 text-slate-500" colSpan={2}>
                Loading categories...
              </TableCell>
            </TableRow>
          ) : categories.length > 0 ? (
            categories.map((category) => (
              <TableRow
                key={category.id}
                className="cursor-pointer bg-white transition-colors hover:bg-slate-50/70"
                onClick={() => onSelect(category)}
              >
                <TableCell className="px-6 py-4 align-top font-medium text-slate-800">
                  {category.categoryName}
                </TableCell>
                <TableCell className="px-6 py-4 align-top text-slate-600">
                  {category.description || "-"}
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell className="px-6 py-10 text-center text-slate-500" colSpan={2}>
                No categories found. Use the add button to create the first one.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>

      <div className="flex justify-end border-t border-slate-200 bg-white px-4 py-4 sm:px-6">
        <Button
          className="h-11 rounded-md bg-blue-600 px-6 text-base font-semibold text-white shadow-sm hover:bg-blue-700"
          onClick={onAdd}
        >
          <Plus className="size-4" />
          Add category
        </Button>
      </div>
    </div>
  );
}