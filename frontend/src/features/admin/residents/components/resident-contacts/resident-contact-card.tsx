import { Edit3, Mail, Phone, ShieldCheck, Trash2, WalletCards } from "lucide-react";

import type { ResidentContact } from "../../types/resident-contact.types";

interface ResidentContactCardProps {
    contact: ResidentContact;
    onEdit: (contact: ResidentContact) => void;
    onRemove: (contact: ResidentContact) => void;
}

export default function ResidentContactCard({
    contact,
    onEdit,
    onRemove,
}: ResidentContactCardProps) {
    const relationship = contact.relationshipType.replaceAll("_", " ");

    return (
        <article className="group rounded-2xl border border-gray-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:border-blue-200 hover:shadow-md">
            <div className="flex items-start justify-between gap-3">
                <div className="min-w-0">
                    <h3 className="truncate text-base font-semibold text-gray-900">
                        {contact.fullName}
                    </h3>
                    <p className="mt-1 text-sm capitalize text-gray-500">{relationship}</p>
                </div>

                {contact.isPrimary && (
                    <span className="shrink-0 rounded-full bg-blue-50 px-2.5 py-1 text-xs font-semibold text-blue-700 ring-1 ring-inset ring-blue-200">
                        Primary
                    </span>
                )}
            </div>

            <div className="mt-5 space-y-3 text-sm">
                <div className="flex items-center gap-3 text-gray-700">
                    <Phone className="h-4 w-4 shrink-0 text-gray-400" />
                    <span className="truncate">{contact.phonePrimary}</span>
                </div>

                <div className="flex items-center gap-3 text-gray-700">
                    <Mail className="h-4 w-4 shrink-0 text-gray-400" />
                    <span className="truncate">{contact.email ?? "—"}</span>
                </div>

                <div className="flex items-center gap-3 text-gray-700">
                    <WalletCards className="h-4 w-4 shrink-0 text-gray-400" />
                    <span>
                        Financial responsibility:{" "}
                        <strong className="font-semibold text-gray-900">
                            {contact.financialResponsibilityPct}%
                        </strong>
                    </span>
                </div>
            </div>

            {(contact.isEmergencyContact || contact.isGuarantor) && (
                <div className="mt-5 flex flex-wrap gap-2 border-t border-gray-100 pt-4">
                    {contact.isEmergencyContact && (
                        <span className="rounded-full bg-orange-50 px-2.5 py-1 text-xs font-medium text-orange-700 ring-1 ring-inset ring-orange-200">
                            Emergency
                        </span>
                    )}

                    {contact.isGuarantor && (
                        <span className="inline-flex items-center gap-1 rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-medium text-emerald-700 ring-1 ring-inset ring-emerald-200">
                            <ShieldCheck className="h-3.5 w-3.5" />
                            Guarantor
                        </span>
                    )}
                </div>
            )}

            <div className="mt-5 grid grid-cols-2 gap-2">
                <button
                    type="button"
                    onClick={() => onEdit(contact)}
                    className="inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 px-3 py-2 text-sm font-medium text-gray-700 transition hover:border-blue-300 hover:bg-blue-50 hover:text-blue-700"
                >
                    <Edit3 className="h-4 w-4" />
                    Edit
                </button>

                <button
                    type="button"
                    onClick={() => onRemove(contact)}
                    className="inline-flex items-center justify-center gap-2 rounded-lg border border-red-200 px-3 py-2 text-sm font-medium text-red-600 transition hover:bg-red-50"
                >
                    <Trash2 className="h-4 w-4" />
                    Remove
                </button>
            </div>
        </article>
    );
}