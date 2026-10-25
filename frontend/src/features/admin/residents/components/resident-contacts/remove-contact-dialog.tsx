import { UserRoundX } from "lucide-react";

import type { ResidentContact } from "../../types/resident-contact.types";

interface RemoveContactDialogProps {
    contact: ResidentContact | null;
    deleting: boolean;
    onCancel: () => void;
    onConfirm: () => Promise<void> | void;
}

export default function RemoveContactDialog({
    contact,
    deleting,
    onCancel,
    onConfirm,
}: RemoveContactDialogProps) {
    if (!contact) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-[2px]">
            <div
                role="dialog"
                aria-modal="true"
                aria-labelledby="remove-contact-title"
                className="w-full max-w-md rounded-2xl bg-white shadow-2xl"
            >
                <div className="p-6">
                    <div className="flex items-start gap-4">
                        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-red-100 text-red-600">
                            <UserRoundX size={20} />
                        </div>

                        <div className="flex-1">
                            <h2
                                id="remove-contact-title"
                                className="text-lg font-semibold text-gray-900"
                            >
                                Remove contact from resident?
                            </h2>

                            <p className="mt-2 text-sm leading-6 text-gray-600">
                                The relationship with{" "}
                                <span className="font-semibold text-gray-900">
                                    {contact.fullName}
                                </span>{" "}
                                will be removed. The Contact itself will remain
                                available in the system.
                            </p>
                        </div>
                    </div>
                </div>

                <div className="flex justify-end gap-3 border-t border-gray-200 bg-gray-50 px-6 py-4">
                    <button
                        type="button"
                        onClick={onCancel}
                        disabled={deleting}
                        autoFocus
                        className="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-100 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        Cancel
                    </button>

                    <button
                        type="button"
                        onClick={() => void onConfirm()}
                        disabled={deleting}
                        className="inline-flex items-center gap-2 rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {deleting && (
                            <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/40 border-t-white" />
                        )}
                        {deleting
                            ? "Removing..."
                            : "Remove relationship"}
                    </button>
                </div>
            </div>
        </div>
    );
}
