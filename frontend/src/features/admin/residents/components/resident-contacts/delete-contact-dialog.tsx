import { AlertTriangle, Trash2 } from "lucide-react";

import type { ContactListItem } from "../../types/contact.types";

interface DeleteContactDialogProps {
    open: boolean;
    contact: ContactListItem | null;
    deleting?: boolean;
    onCancel: () => void;
    onConfirm: () => void;
}

export default function DeleteContactDialog({
    open,
    contact,
    deleting = false,
    onCancel,
    onConfirm,
}: DeleteContactDialogProps) {
    if (!open || !contact) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 p-4 backdrop-blur-[2px]">
            <div
                role="dialog"
                aria-modal="true"
                aria-labelledby="delete-contact-title"
                className="w-full max-w-md rounded-2xl bg-white shadow-2xl"
            >
                <div className="p-6">
                    <div className="flex items-start gap-4">
                        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-red-100 text-red-600">
                            <Trash2 size={20} />
                        </div>

                        <div className="flex-1">
                            <h2
                                id="delete-contact-title"
                                className="text-lg font-semibold text-gray-900"
                            >
                                Delete Contact
                            </h2>

                            <p className="mt-2 text-sm leading-6 text-gray-600">
                                Are you sure you want to
                                soft delete{" "}
                                <span className="font-semibold text-gray-900">
                                    {contact.fullName}
                                </span>
                                ?
                            </p>

                            <p className="mt-2 text-sm text-gray-500">
                                The record remains in the
                                database and can be viewed
                                using Include deleted.
                            </p>

                            <div className="mt-4 flex items-start gap-2.5 rounded-lg border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
                                <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0 text-amber-600" />
                                Deletion will be blocked if
                                this contact is the guarantor
                                of an active resident.
                            </div>
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
                        onClick={onConfirm}
                        disabled={deleting}
                        className="inline-flex items-center gap-2 rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {deleting ? (
                            <>
                                <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/40 border-t-white" />
                                Deleting...
                            </>
                        ) : (
                            <>
                                <Trash2 size={16} />
                                Delete Contact
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}
