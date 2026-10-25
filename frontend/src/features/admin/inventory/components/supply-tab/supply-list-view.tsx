import { Plus } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import type { SupplyItem } from "../../store/use-supply-store";
import type { SupplyStatus } from "../../api/supply-api";

interface SupplyMetrics {
    total: number;
    available: number;
    lowStock: number;
    outOfStock: number;
}

interface SupplyListViewProps {
    supplies: SupplyItem[];
    isLoading: boolean;
    metrics: SupplyMetrics;
    searchTerm: string;
    statusFilter: SupplyStatus | "ALL";
    onSearchTermChange: (value: string) => void;
    onStatusFilterChange: (value: SupplyStatus | "ALL") => void;
    onAdd: () => void;
    onSelect: (supply: SupplyItem) => void;
}

const statusLabelMap: Record<SupplyStatus, string> = {
    OK: "OK",
    LOW_STOCK: "Low stock",
    OUT_OF_STOCK: "Out of stock",
}

const statusVariantClassMap: Record<SupplyStatus, string> = {
    OK: "border-emerald-200 bg-emerald-50 text-emerald-700",
    LOW_STOCK: "border-amber-200 bg-amber-50 text-amber-700",
    OUT_OF_STOCK: "border-red-200 bg-red-50 text-red-700",
}

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
}

export function SupplyListView({
    supplies,
    isLoading,
    metrics,
    searchTerm,
    statusFilter,
    onSearchTermChange,
    onStatusFilterChange,
    onAdd,
    onSelect,
}: SupplyListViewProps) {
    return (
        <div className="space-y-5 rounded-2xl border border-slate-200 bg-white p-4 shadow-[0_1px_0_rgba(15,23,42,0.03)] sm:p-6">
            <div className="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
                <div>
                    <h3 className="text-lg font-semibold text-slate-900">Consumable supplies</h3>
                    <p className="text-sm text-slate-500">Track supplies, status, and facility assignment.</p>
                </div>

                <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
                    <div className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
                        <div className="text-xs font-semibold uppercase tracking-wide text-slate-500">Total</div>
                        <div className="mt-1 text-2xl font-semibold text-slate-900">{metrics.total}</div>
                    </div>
                    <div className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3">
                        <div className="text-xs font-semibold uppercase tracking-wide text-emerald-700">Available</div>
                        <div className="mt-1 text-2xl font-semibold text-emerald-800">{metrics.available}</div>
                    </div>
                    <div className="rounded-xl border border-blue-200 bg-blue-50 px-4 py-3">
                        <div className="text-xs font-semibold uppercase tracking-wide text-blue-700">Low stock</div>
                        <div className="mt-1 text-2xl font-semibold text-blue-800">{metrics.lowStock}</div>
                    </div>
                    <div className="rounded-xl border border-amber-200 bg-amber-50 px-4 py-3">
                        <div className="text-xs font-semibold uppercase tracking-wide text-amber-700">Out of stock</div>
                        <div className="mt-1 text-2xl font-semibold text-amber-800">{metrics.outOfStock}</div>
                    </div>
                </div>
            </div>

            <div className="grid gap-3 rounded-xl border border-slate-200 bg-slate-50/40 p-4 lg:grid-cols-[1.4fr_0.7fr_auto] lg:items-end">
                <label className="space-y-2">
                    <span className="text-sm font-semibold text-slate-700">Search supply</span>
                    <Input
                        className="h-11 rounded-md border-slate-300 bg-white text-slate-900"
                        placeholder="Search by name, asset tag, category, or facility"
                        value={searchTerm}
                        onChange={(event) => onSearchTermChange(event.target.value)}
                    />
                </label>

                <label className="space-y-2">
                    <span className="text-sm font-semibold text-slate-700">Status</span>
                    <select
                        className="h-11 w-full rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none focus:border-slate-400"
                        value={statusFilter}
                        onChange={(event) => onStatusFilterChange(event.target.value as SupplyStatus | "ALL")}
                    >
                        <option value="ALL">All statuses</option>
                        <option value="OK">OK</option>
                        <option value="LOW_STOCK">Low stock</option>
                        <option value="OUT_OF_STOCK">Out of stock</option>
                        <option value="RETIRED">Retired</option>
                    </select>
                </label>

                <Button className="h-11 rounded-md bg-blue-600 px-6 text-base font-semibold text-white shadow-sm hover:bg-blue-700" onClick={onAdd}>
                    <Plus className="size-4" />
                    Add supply
                </Button>
            </div>

            <div className="overflow-hidden rounded-xl border border-slate-200 bg-slate-50/40">
                <Table>
                    <TableHeader className="bg-slate-50/80">
                        <TableRow className="hover:bg-transparent">
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Supply</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Category</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Stock on hand</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Total</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Reorder Threshold</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Unit Cost</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Private pay rate</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Status</TableHead>
                            <TableHead className="px-6 py-3 text-sm font-semibold text-slate-700">Facility</TableHead>
                            <TableHead className="px-6 py-3 text-right text-sm font-semibold text-slate-700">Action</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell className="px-6 py-6 text-slate-500" colSpan={10}>
                                    Loading supplies...
                                </TableCell>
                            </TableRow>
                        ) : supplies.length > 0 ? (
                            supplies.map((item) => (
                                <TableRow key={item.id} className="cursor-pointer bg-white transition-colors hover:bg-slate-50/70">
                                    <TableCell className="px-6 py-4 align-top font-medium text-slate-800" onClick={() => onSelect(item)}>
                                        {item.itemName}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {item.category.name}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {item.stockOnHand}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {item.total}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {item.reorderThreshold}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {formatCurrency(item.unitCost)}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {formatCurrency(item.privatePayRate)}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top" onClick={() => onSelect(item)}>
                                        <Badge className={statusVariantClassMap[item.status]} variant="outline">
                                            {statusLabelMap[item.status]}
                                        </Badge>
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-slate-600" onClick={() => onSelect(item)}>
                                        {item.facility.name}
                                    </TableCell>
                                    <TableCell className="px-6 py-4 align-top text-right">
                                        <Button
                                            className="h-9 rounded-md border-slate-300 bg-white px-4 text-sm font-semibold text-slate-700 shadow-none hover:bg-slate-50"
                                            onClick={() => onSelect(item)}
                                            type="button"
                                            variant="outline"
                                        >
                                            View
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell className="px-6 py-10 text-center text-slate-500" colSpan={10}>
                                    No supplies found for the selected filters.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
};