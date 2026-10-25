import { Edit3, Loader2, Trash2, UserRoundX } from "lucide-react";

import type { ResidentContact } from "../../types/resident-contact.types";
import ResidentContactCard from "./resident-contact-card";

interface ResidentContactTableProps {
    contacts: ResidentContact[];
    loading: boolean;
    onEdit: (contact: ResidentContact) => void;
    onRemove: (contact: ResidentContact) => void;
}

const badgeClassName: Record<string, string> = {
    Primary: "border-blue-200 bg-blue-50 text-blue-700",
    Emergency: "border-orange-200 bg-orange-50 text-orange-700",
    Guarantor: "border-emerald-200 bg-emerald-50 text-emerald-700",
};

function BooleanBadge({ value, label }: { value: boolean; label: string }) {
    if (!value) return <span className="text-sm text-gray-400">—</span>;

    return (
        <span
            className={`inline-flex rounded-full border px-2.5 py-1 text-xs font-medium ${badgeClassName[label]}`}
        >
            {label}
        </span>
    );
}

export default function ResidentContactTable({
    contacts,
    loading,
    onEdit,
    onRemove,
}: ResidentContactTableProps) {
    if (loading) {
        return (
            <div className="flex items-center justify-center gap-2 rounded-2xl border border-gray-200 bg-white p-10 text-sm text-gray-500 shadow-sm">
                <Loader2 className="h-4 w-4 animate-spin" />
                Loading resident contacts...
            </div>
        );
    }

    if (contacts.length === 0) {
        return (
            <div className="rounded-2xl border border-dashed border-gray-300 bg-gray-50 p-10 text-center">
                <UserRoundX className="mx-auto h-10 w-10 text-gray-400" />
                <p className="mt-3 font-semibold text-gray-800">No contacts linked</p>
                <p className="mt-1 text-sm text-gray-500">
                    Add an existing Contact to test API 35.
                </p>
            </div>
        );
    }

    return (
        <>
            <div className="grid gap-4 md:hidden">
                {contacts.map((contact) => (
                    <ResidentContactCard
                        key={contact.residentContactId}
                        contact={contact}
                        onEdit={onEdit}
                        onRemove={onRemove}
                    />
                ))}
            </div>

            <div className="hidden overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm md:block">
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                            <tr>
                                {[
                                    "Contact",
                                    "Relationship",
                                    "Primary",
                                    "Emergency",
                                    "Guarantor",
                                    "Financial",
                                ].map((heading) => (
                                    <th
                                        key={heading}
                                        className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500"
                                    >
                                        {heading}
                                    </th>
                                ))}

                                <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
                                    Actions
                                </th>
                            </tr>
                        </thead>

                        <tbody className="divide-y divide-gray-100">
                            {contacts.map((contact) => (
                                <tr
                                    key={contact.residentContactId}
                                    className="transition-colors hover:bg-gray-50"
                                >
                                    <td className="px-4 py-4">
                                        <p className="font-semibold text-gray-900">
                                            {contact.fullName}
                                        </p>
                                        <p className="mt-1 text-sm text-gray-500">
                                            {contact.phonePrimary}
                                        </p>
                                        <p className="text-sm text-gray-500">
                                            {contact.email ?? "—"}
                                        </p>
                                    </td>

                                    <td className="px-4 py-4 text-sm capitalize text-gray-700">
                                        {contact.relationshipType.replaceAll("_", " ")}
                                    </td>

                                    <td className="px-4 py-4">
                                        <BooleanBadge value={contact.isPrimary} label="Primary" />
                                    </td>

                                    <td className="px-4 py-4">
                                        <BooleanBadge
                                            value={contact.isEmergencyContact}
                                            label="Emergency"
                                        />
                                    </td>

                                    <td className="px-4 py-4">
                                        <BooleanBadge
                                            value={contact.isGuarantor}
                                            label="Guarantor"
                                        />
                                    </td>

                                    <td className="px-4 py-4">
                                        <span className="inline-flex rounded-lg bg-gray-100 px-2.5 py-1 text-sm font-semibold text-gray-800">
                                            {contact.financialResponsibilityPct}%
                                        </span>
                                    </td>

                                    <td className="px-4 py-4">
                                        <div className="flex justify-end gap-2">
                                            <button
                                                type="button"
                                                onClick={() => onEdit(contact)}
                                                className="inline-flex items-center gap-1.5 rounded-lg border border-gray-300 px-3 py-1.5 text-sm font-medium text-gray-700 transition hover:border-blue-300 hover:bg-blue-50 hover:text-blue-700"
                                            >
                                                <Edit3 className="h-4 w-4" />
                                                Edit
                                            </button>

                                            <button
                                                type="button"
                                                onClick={() => onRemove(contact)}
                                                className="inline-flex items-center gap-1.5 rounded-lg border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50"
                                            >
                                                <Trash2 className="h-4 w-4" />
                                                Remove
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
    );
}