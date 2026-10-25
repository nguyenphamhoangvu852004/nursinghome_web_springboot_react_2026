import { useRef, useState } from "react";
import {
    AlertTriangle,
    CheckCircle2,
    Download,
    FileSpreadsheet,
    Loader2,
    RotateCcw,
    Upload,
    X,
    XCircle,
} from "lucide-react";
import { useSeedDemoData } from "../hooks/use-demo-data";
import { exportDemoData } from "../services/demo-data.service";
import type { DemoDataset } from "../types/demo-data.type";

const demoDatasets: DemoDataset[] = [
    {
        name: "Residents",
        records: "~20",
        lastSeeded: "—",
        status: "Import from CSV",
    },
    {
        name: "Users",
        records: "~4 demo staff",
        lastSeeded: "—",
        status: "Auto generated",
    },
    {
        name: "Care Plans",
        records: "~1 per resident",
        lastSeeded: "—",
        status: "Auto generated",
    },
    {
        name: "Medication Orders",
        records: "~40–45",
        lastSeeded: "—",
        status: "Auto generated",
    },
    {
        name: "Incidents",
        records: "~6–8",
        lastSeeded: "—",
        status: "Auto generated",
    },
    {
        name: "Facilities / Rooms / Beds",
        records: "Lookup / create if missing",
        lastSeeded: "—",
        status: "Reference data",
    },
    {
        name: "Care Levels",
        records: "Lookup / create if missing",
        lastSeeded: "—",
        status: "Reference data",
    },
];

const getStatusClassName = (status: string) => {
    if (status === "Import from CSV") {
        return "border-blue-200 bg-blue-50 text-blue-700";
    }

    if (status === "Auto generated") {
        return "border-emerald-200 bg-emerald-50 text-emerald-700";
    }

    return "border-gray-200 bg-gray-50 text-gray-600";
};

const DemoDataPage = () => {
    const fileInputRef = useRef<HTMLInputElement | null>(null);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [exporting, setExporting] = useState(false);
    const [exportingKey, setExportingKey] = useState<string | null>(null);
    const [confirmReset, setConfirmReset] = useState(false);

    const seedMutation = useSeedDemoData();

    const handleLoad = () => {
        fileInputRef.current?.click();
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];

        if (!file) return;

        setSelectedFile(file);
        seedMutation.mutate(file);

        e.target.value = "";
    };

    const handleExport = async (
        type: "ResidentList" | "IncidentLog",
        format: "csv" | "pdf"
    ) => {
        try {
            setExporting(true);
            setExportingKey(`${type}-${format}`);
            await exportDemoData(type, format);
        } finally {
            setExporting(false);
            setExportingKey(null);
        }
    };

    return (
        <div className="mx-auto max-w-6xl space-y-6 p-4 sm:p-6">
            {/* Header */}
            <div className="space-y-1">
                <p className="text-sm text-gray-500">Admin &gt; Data</p>
                <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
                    Demo Data Seeder
                </h1>
                <p className="max-w-2xl text-sm text-gray-500">
                    Upload one resident fixture CSV. The backend will create residents,
                    users, care plans, medication orders, incidents, and required
                    reference links.
                </p>
            </div>

            <input
                ref={fileInputRef}
                type="file"
                accept=".csv"
                className="hidden"
                onChange={handleFileChange}
            />

            {/* Environment warning */}
            <div className="flex flex-col gap-3 rounded-lg border border-amber-300 bg-amber-50 sm:flex-row sm:items-center sm:justify-between">
                <div className="flex items-start gap-3 px-4 py-3 sm:py-4">
                    <AlertTriangle className="mt-0.5 h-5 w-5 shrink-0 text-amber-600" />
                    <p className="text-sm font-medium text-amber-800">
                        Dev / Staging only — not available in Production. All actions are
                        simulated (NFR-05).
                    </p>
                </div>

                <button
                    onClick={() => setConfirmReset(true)}
                    className="flex h-11 items-center justify-center gap-2 border-t border-amber-300 px-6 text-sm font-medium text-amber-700 transition-colors hover:bg-amber-100 sm:h-full sm:border-l sm:border-t-0"
                >
                    <RotateCcw className="h-4 w-4" />
                    Reset All Demo Data
                </button>
            </div>

            {/* Inline reset confirmation */}
            {confirmReset && (
                <div className="flex flex-col gap-3 rounded-lg border border-red-200 bg-red-50 p-4 sm:flex-row sm:items-center sm:justify-between">
                    <p className="text-sm text-red-700">
                        This will remove all seeded demo records. Are you sure?
                    </p>
                    <div className="flex gap-2">
                        <button
                            onClick={() => setConfirmReset(false)}
                            className="h-9 rounded-md border border-gray-300 bg-white px-4 text-sm font-medium text-gray-700 hover:bg-gray-50"
                        >
                            Cancel
                        </button>
                        <button
                            onClick={() => setConfirmReset(false)}
                            className="h-9 rounded-md bg-red-600 px-4 text-sm font-medium text-white hover:bg-red-700"
                        >
                            Yes, reset
                        </button>
                    </div>
                </div>
            )}

            {/* Export section */}
            <div className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm">
                <h2 className="mb-3 flex items-center gap-2 text-lg font-bold text-gray-900">
                    <Download className="h-5 w-5 text-gray-400" />
                    Export Data
                </h2>

                <div className="flex flex-wrap gap-3">
                    {[
                        { type: "ResidentList" as const, format: "csv" as const, label: "Export Residents CSV" },
                        { type: "ResidentList" as const, format: "pdf" as const, label: "Export Residents PDF" },
                        { type: "IncidentLog" as const, format: "csv" as const, label: "Export Incidents CSV" },
                        { type: "IncidentLog" as const, format: "pdf" as const, label: "Export Incidents PDF" },
                    ].map(({ type, format, label }) => {
                        const key = `${type}-${format}`;
                        const isLoading = exportingKey === key;

                        return (
                            <button
                                key={key}
                                disabled={exporting}
                                onClick={() => handleExport(type, format)}
                                className="flex h-10 items-center gap-2 rounded-md border border-gray-300 bg-white px-4 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                                {isLoading ? (
                                    <Loader2 className="h-4 w-4 animate-spin text-gray-500" />
                                ) : (
                                    <Download className="h-4 w-4 text-gray-400" />
                                )}
                                {label}
                            </button>
                        );
                    })}
                </div>
            </div>

            {/* Selected file */}
            {selectedFile && (
                <div className="flex items-center gap-2 rounded-md border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-600">
                    <FileSpreadsheet className="h-4 w-4 text-gray-400" />
                    Selected file: <span className="font-medium text-gray-800">{selectedFile.name}</span>
                    <button
                        onClick={() => setSelectedFile(null)}
                        className="ml-auto text-gray-400 hover:text-gray-600"
                        aria-label="Clear selected file"
                    >
                        <X className="h-4 w-4" />
                    </button>
                </div>
            )}

            {/* Feedback banners */}
            {seedMutation.isSuccess && (
                <div className="flex items-start gap-3 rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-800">
                    <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-emerald-600" />
                    <span>
                        Seed completed: <strong>{seedMutation.data.residents_loaded}</strong> residents,{" "}
                        <strong>{seedMutation.data.medication_orders_loaded}</strong> medication orders,{" "}
                        <strong>{seedMutation.data.incidents_loaded}</strong> incidents.
                    </span>
                </div>
            )}

            {seedMutation.isError && (
                <div className="flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
                    <XCircle className="mt-0.5 h-5 w-5 shrink-0 text-red-600" />
                    Failed to seed demo data. Check the CSV format and try again.
                </div>
            )}

            {/* Dataset table */}
            <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
                <div className="overflow-x-auto">
                    <table className="w-full text-left text-sm">
                        <thead className="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
                            <tr>
                                <th className="px-4 py-3 font-semibold">Dataset</th>
                                <th className="px-4 py-3 font-semibold">Records</th>
                                <th className="px-4 py-3 font-semibold">Last Seeded</th>
                                <th className="px-4 py-3 font-semibold">Status</th>
                                <th className="px-4 py-3 font-semibold"></th>
                            </tr>
                        </thead>

                        <tbody className="divide-y divide-gray-100">
                            {demoDatasets.map((dataset) => (
                                <tr
                                    key={dataset.name}
                                    className="transition-colors hover:bg-gray-50"
                                >
                                    <td className="whitespace-nowrap px-4 py-3.5 font-semibold text-gray-900">
                                        {dataset.name}
                                    </td>

                                    <td className="px-4 py-3.5 text-gray-600">
                                        {dataset.records}
                                    </td>

                                    <td className="px-4 py-3.5 text-gray-400">
                                        {dataset.lastSeeded}
                                    </td>

                                    <td className="px-4 py-3.5">
                                        <span
                                            className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-medium ${getStatusClassName(
                                                dataset.status
                                            )}`}
                                        >
                                            {dataset.status}
                                        </span>
                                    </td>

                                    <td className="px-4 py-3">
                                        <div className="flex justify-end gap-2">
                                            <button
                                                onClick={handleLoad}
                                                disabled={seedMutation.isPending}
                                                className="flex h-9 w-24 items-center justify-center gap-1.5 rounded-md border border-gray-300 bg-white text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-60"
                                            >
                                                {seedMutation.isPending ? (
                                                    <Loader2 className="h-3.5 w-3.5 animate-spin" />
                                                ) : (
                                                    <Upload className="h-3.5 w-3.5 text-gray-400" />
                                                )}
                                                {seedMutation.isPending ? "Loading" : "Load"}
                                            </button>

                                            <button
                                                disabled
                                                className="h-9 w-24 rounded-md border border-gray-200 bg-gray-50 text-sm text-gray-400"
                                            >
                                                Clear
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <p className="max-w-4xl text-xs leading-relaxed text-gray-400">
                The CSV only contains resident seed inputs. Users, care plans,
                medication orders, incidents, facilities/rooms/beds, and care levels are
                generated or linked by the backend based on existing database data.
            </p>
        </div>
    );
};

export default DemoDataPage;
