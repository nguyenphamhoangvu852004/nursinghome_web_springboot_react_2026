import { useEffect, useState } from "react";
import {
    AlertCircle,
    CheckCircle2,
    Loader2,
    ShieldAlert,
} from "lucide-react";
import {
    useStaffingRatio,
    useUpdateStaffingRatio,
} from "../hooks/use-staffing-ratio";

const shifts = [
    {
        name: "Day (07:00–15:00)",
        cnaHours: "1.5 hrs",
        nurseHours: "0.9 hrs",
        subtotal: "2.4 hrs",
    },
    {
        name: "Evening (15:00–23:00)",
        cnaHours: "0.7 hrs",
        nurseHours: "0.5 hrs",
        subtotal: "1.2 hrs",
    },
    {
        name: "Night (23:00–07:00)",
        cnaHours: "0.5 hrs",
        nurseHours: "0.3 hrs",
        subtotal: "0.8 hrs",
    },
];

const StaffingRatioPage = () => {
    const { data, isLoading, isError } = useStaffingRatio();
    const updateMutation = useUpdateStaffingRatio();

    const [minHours, setMinHours] = useState("");
    const [warnBelowPercentage, setWarnBelowPercentage] = useState("");

    useEffect(() => {
        if (data) {
            setMinHours(String(data.minHrsPerResidentDay));
            setWarnBelowPercentage(String(data.warnBelowPercentage));
        }
    }, [data]);

    const isDirty =
        data &&
        (minHours !== String(data.minHrsPerResidentDay) ||
            warnBelowPercentage !== String(data.warnBelowPercentage));

    const handleSave = () => {
        updateMutation.mutate({
            minHrsPerResidentDay: Number(minHours),
            warnBelowPercentage: Number(warnBelowPercentage),
        });
    };

    if (isLoading) {
        return (
            <div className="flex items-center justify-center gap-2 p-10 text-sm text-gray-500">
                <Loader2 className="h-4 w-4 animate-spin" />
                Loading staffing ratio...
            </div>
        );
    }

    if (isError) {
        return (
            <div className="mx-auto mt-6 flex max-w-md items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
                <AlertCircle className="mt-0.5 h-5 w-5 shrink-0 text-red-600" />
                Failed to load staffing ratio. Please refresh the page or try again
                later.
            </div>
        );
    }

    return (
        <div className="flex min-h-full flex-col">
            <div className="mx-auto w-full max-w-6xl flex-1 space-y-6 p-4 sm:p-6">
                <div className="space-y-1">
                    <p className="text-sm text-gray-500">Admin &gt; Staffing</p>
                    <h1 className="text-2xl font-bold tracking-tight text-gray-900 sm:text-3xl">
                        Staffing Ratio Configuration
                    </h1>
                    <p className="max-w-2xl text-sm text-gray-500">
                        California SNF minimum: {minHours} direct care hours per resident
                        per day (BR-01)
                    </p>
                </div>

                <div className="rounded-xl border border-gray-200 bg-white p-5 shadow-sm">
                    <h2 className="mb-4 text-lg font-bold text-gray-900 sm:text-xl">
                        Minimum Requirement
                    </h2>

                    <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">
                                Target State
                            </label>
                            <input
                                value="California"
                                readOnly
                                className="h-11 w-full rounded-md border border-gray-300 bg-gray-50 px-4 text-sm text-gray-600"
                            />
                        </div>

                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">
                                Minimum Direct Care Hours
                            </label>
                            <div className="flex h-11 items-center rounded-md border border-gray-300 bg-white px-4 transition-colors focus-within:border-blue-400 focus-within:ring-2 focus-within:ring-blue-100">
                                <input
                                    value={minHours}
                                    onChange={(e) => setMinHours(e.target.value)}
                                    inputMode="decimal"
                                    className="w-full text-sm text-gray-700 outline-none"
                                />
                                <span className="shrink-0 text-xs text-gray-400">
                                    hrs/resident/day
                                </span>
                            </div>
                        </div>

                        <div>
                            <label className="mb-2 block text-sm font-semibold text-gray-700">
                                Warn Below Percentage
                            </label>
                            <div className="flex h-11 items-center rounded-md border border-gray-300 bg-white px-4 transition-colors focus-within:border-blue-400 focus-within:ring-2 focus-within:ring-blue-100">
                                <input
                                    value={warnBelowPercentage}
                                    onChange={(e) => setWarnBelowPercentage(e.target.value)}
                                    inputMode="decimal"
                                    className="w-full text-sm text-gray-700 outline-none"
                                />
                                <span className="shrink-0 text-xs text-gray-400">%</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div>
                    <h2 className="mb-4 text-lg font-bold text-gray-900 sm:text-xl">
                        Shift Breakdown
                    </h2>

                    <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
                        <div className="overflow-x-auto">
                            <table className="w-full text-left text-sm">
                                <thead className="bg-gray-50 text-xs uppercase tracking-wide text-gray-500">
                                    <tr>
                                        <th className="px-4 py-3 font-semibold">Shift</th>
                                        <th className="px-4 py-3 font-semibold">
                                            Required CNA hrs
                                        </th>
                                        <th className="px-4 py-3 font-semibold">
                                            Required Nurse hrs
                                        </th>
                                        <th className="px-4 py-3 font-semibold">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {shifts.map((shift) => (
                                        <tr
                                            key={shift.name}
                                            className="transition-colors hover:bg-gray-50"
                                        >
                                            <td className="whitespace-nowrap px-4 py-3.5 text-gray-700">
                                                {shift.name}
                                            </td>
                                            <td className="px-4 py-3.5 text-gray-500">
                                                {shift.cnaHours}
                                            </td>
                                            <td className="px-4 py-3.5 text-gray-500">
                                                {shift.nurseHours}
                                            </td>
                                            <td className="px-4 py-3.5 font-semibold text-gray-900">
                                                {shift.subtotal}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                                <tfoot>
                                    <tr className="border-t border-gray-200 bg-gray-50">
                                        <td className="px-4 py-3 font-semibold text-gray-900">
                                            Sum of shifts
                                        </td>
                                        <td colSpan={2} />
                                        <td className="px-4 py-3 font-semibold text-gray-900">
                                            4.4 hrs/resident/day
                                        </td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>

                <div className="rounded-xl border border-emerald-200 bg-emerald-50 p-5 text-sm text-emerald-800">
                    <h3 className="mb-3 flex items-center gap-2 font-bold">
                        <ShieldAlert className="h-4 w-4 text-emerald-600" />
                        Current Compliance (simulated)
                    </h3>
                    <p className="text-emerald-700">
                        Facility ID: {data?.facilityId} · Warning threshold:{" "}
                        {warnBelowPercentage}%
                    </p>
                    <p className="mt-1 flex items-center gap-1.5 font-semibold">
                        <CheckCircle2 className="h-4 w-4 text-emerald-600" />
                        Minimum requirement: {minHours} hrs/resident/day → Configured
                    </p>
                </div>

                {updateMutation.isSuccess && (
                    <div className="flex items-start gap-3 rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-800">
                        <CheckCircle2 className="mt-0.5 h-5 w-5 shrink-0 text-emerald-600" />
                        Staffing ratio settings saved.
                    </div>
                )}

                {updateMutation.isError && (
                    <div className="flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
                        <AlertCircle className="mt-0.5 h-5 w-5 shrink-0 text-red-600" />
                        Failed to save staffing ratio settings. Please try again.
                    </div>
                )}
            </div>

            <div className="sticky bottom-0 flex flex-col items-center gap-3 border-t border-gray-200 bg-white p-4">
                {isDirty && (
                    <span className="text-sm text-amber-600">
                        You have unsaved changes.
                    </span>
                )}

                <div className="flex w-full flex-col gap-3 sm:w-auto sm:flex-row">
                    <button
                        onClick={() => {
                            if (data) {
                                setMinHours(String(data.minHrsPerResidentDay));
                                setWarnBelowPercentage(String(data.warnBelowPercentage));
                            }
                        }}
                        className="h-11 rounded-md border border-gray-300 bg-white px-6 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-50"
                    >
                        Cancel
                    </button>

                    <button
                        onClick={handleSave}
                        disabled={updateMutation.isPending || !isDirty}
                        className="flex h-11 items-center justify-center gap-2 rounded-md bg-blue-600 px-6 text-sm font-bold text-white transition-colors hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
                    >
                        {updateMutation.isPending && (
                            <Loader2 className="h-4 w-4 animate-spin" />
                        )}
                        {updateMutation.isPending ? "Saving..." : "Save Changes"}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default StaffingRatioPage;
