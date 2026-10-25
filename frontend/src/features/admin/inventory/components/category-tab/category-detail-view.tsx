import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import type { CategoryItem } from "../../store/use-inventory-category-store";

interface CategoryDetailViewProps {
  category: CategoryItem;
  onDelete: () => void;
  onUpdate: () => void;
  onCancel: () => void;
}

export function CategoryDetailView({ category, onDelete, onUpdate, onCancel }: CategoryDetailViewProps) {
  return (
    <div className="space-y-5 rounded-2xl border border-slate-200 bg-white p-4 shadow-[0_1px_0_rgba(15,23,42,0.03)] sm:p-6">
      <h3 className="text-lg font-semibold text-slate-900">Category Detail</h3>

      <div className="space-y-5 rounded-xl border border-slate-100 bg-slate-50/40 p-4 sm:p-6">
        <div className="space-y-2">
          <Label htmlFor="detail-category-name" className="text-sm font-semibold text-slate-800">
            Category name
          </Label>
          <Input
            id="detail-category-name"
            className="h-11 max-w-[240px] rounded-sm border-slate-300 bg-white text-slate-900"
            disabled
            readOnly
            value={category.categoryName}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="detail-description" className="text-sm font-semibold text-slate-800">
            Description
          </Label>
          <Textarea
            id="detail-description"
            className="min-h-24 rounded-sm border-slate-300 bg-white text-slate-900 sm:min-h-28"
            disabled
            readOnly
            value={category.description}
          />
        </div>
      </div>

      <div className="flex flex-col-reverse gap-3 pt-2 sm:flex-row sm:justify-end">
        <Button
          className="h-11 min-w-32 rounded-md bg-red-600 px-6 text-base font-semibold text-white shadow-sm hover:bg-red-700"
          onClick={onDelete}
          type="button"
        >
          Delete
        </Button>
        <Button
          className="h-11 min-w-32 rounded-md bg-amber-400 px-6 text-base font-semibold text-white shadow-sm hover:bg-amber-500"
          onClick={onUpdate}
          type="button"
        >
          Update
        </Button>
        <Button
          className="h-11 min-w-32 rounded-md border border-slate-300 bg-white px-6 text-base font-semibold text-slate-600 shadow-none hover:bg-slate-50"
          onClick={onCancel}
          type="button"
          variant="outline"
        >
          Cancel
        </Button>
      </div>
    </div>
  );
}
