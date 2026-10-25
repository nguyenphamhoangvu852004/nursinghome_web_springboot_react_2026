import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import type { EquipmentItem } from "../../store/use-equipment-store";

interface EquipmentDetailViewProps {
  equipment: EquipmentItem;
  onDelete: () => void;
  onUpdate: () => void;
  onCancel: () => void;
}

const statusLabelMap = {
  AVAILABLE: "Available",
  IN_SERVICE: "In service",
  UNDER_MAINTENANCE: "Under maintenance",
  RETIRED: "Retired",
} as const;

const statusClassMap = {
  AVAILABLE: "border-emerald-200 bg-emerald-50 text-emerald-700",
  IN_SERVICE: "border-blue-200 bg-blue-50 text-blue-700",
  UNDER_MAINTENANCE: "border-amber-200 bg-amber-50 text-amber-700",
  RETIRED: "border-slate-200 bg-slate-100 text-slate-600",
} as const;

const formatCurrency = (value: string) => {
  const numericValue = Number(value);
  if (Number.isNaN(numericValue)) {
    return value;
  }

  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2,
  }).format(numericValue);
};

export function EquipmentDetailView({ equipment, onDelete, onUpdate, onCancel }: EquipmentDetailViewProps) {
  return (
    <div className="space-y-5 rounded-2xl border border-slate-200 bg-white p-4 shadow-[0_1px_0_rgba(15,23,42,0.03)] sm:p-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h3 className="text-lg font-semibold text-slate-900">Equipment detail</h3>
          <p className="text-sm text-slate-500">Review equipment identity, assignment, and current state.</p>
        </div>
        <Badge className={statusClassMap[equipment.status]} variant="outline">
          {statusLabelMap[equipment.status]}
        </Badge>
      </div>

      <div className="grid gap-5 rounded-xl border border-slate-100 bg-slate-50/40 p-4 sm:p-6 lg:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="detail-equipment-name" className="text-sm font-semibold text-slate-800">
            Equipment name
          </Label>
          <Input
            id="detail-equipment-name"
            className="h-11 rounded-sm border-slate-300 bg-white text-slate-900"
            disabled
            readOnly
            value={equipment.itemName}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="detail-asset-tag" className="text-sm font-semibold text-slate-800">
            Asset tag
          </Label>
          <Input
            id="detail-asset-tag"
            className="h-11 rounded-sm border-slate-300 bg-white text-slate-900"
            disabled
            readOnly
            value={equipment.assetTag}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="detail-category" className="text-sm font-semibold text-slate-800">
            Category
          </Label>
          <Input
            id="detail-category"
            className="h-11 rounded-sm border-slate-300 bg-white text-slate-900"
            disabled
            readOnly
            value={equipment.category.name}
          />
        </div>

        <div className="space-y-2">
          <Label htmlFor="detail-facility" className="text-sm font-semibold text-slate-800">
            Facility
          </Label>
          <Input
            id="detail-facility"
            className="h-11 rounded-sm border-slate-300 bg-white text-slate-900"
            disabled
            readOnly
            value={equipment.facility.name}
          />
        </div>

        <div className="space-y-2 lg:col-span-2">
          <Label htmlFor="detail-unit-value" className="text-sm font-semibold text-slate-800">
            Unit value
          </Label>
          <Input
            id="detail-unit-value"
            className="h-11 max-w-[240px] rounded-sm border-slate-300 bg-white text-slate-900"
            disabled
            readOnly
            value={formatCurrency(equipment.unitValue)}
          />
        </div>

        <div className="space-y-2 lg:col-span-2">
          <Label htmlFor="detail-status" className="text-sm font-semibold text-slate-800">
            Status note
          </Label>
          <Textarea
            id="detail-status"
            className="min-h-24 rounded-sm border-slate-300 bg-white text-slate-900 sm:min-h-28"
            disabled
            readOnly
            value={`${statusLabelMap[equipment.status]} equipment is currently tracked in the inventory register.`}
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