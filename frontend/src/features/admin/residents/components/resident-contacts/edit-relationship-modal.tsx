import { X } from "lucide-react";

import ResidentContactForm from "../../forms/resident-contact-form";

import type {
    ResidentContact,
    ResidentContactRelationshipFormData,
} from "../../types/resident-contact.types";

interface EditRelationshipModalProps {
    contact: ResidentContact | null;
    submitting?: boolean;
    onClose: () => void;
    onSubmit: (
        payload: ResidentContactRelationshipFormData,
    ) => Promise<void>;
}

export default function EditRelationshipModal({
    contact,
    submitting = false,
    onClose,
    onSubmit,
}: EditRelationshipModalProps) {
    if (!contact) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-[2px]">
            <div
                role="dialog"
                aria-modal="true"
                aria-labelledby="edit-relationship-title"
                className="max-h-[90vh] w-full max-w-xl overflow-y-auto rounded-2xl bg-white shadow-xl"
            >
                <div className="flex items-start justify-between border-b border-gray-200 px-6 py-4">
                    <div>
                        <h2
                            id="edit-relationship-title"
                            className="text-lg font-semibold text-gray-900"
                        >
                            Edit Contact Relationship
                        </h2>

                        <p className="mt-1 text-sm text-gray-500">
                            {contact.fullName}
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={onClose}
                        disabled={submitting}
                        className="rounded-lg p-2 text-gray-500 transition-colors hover:bg-gray-100 hover:text-gray-700 disabled:cursor-not-allowed disabled:opacity-50"
                        aria-label="Close"
                    >
                        <X size={20} />
                    </button>
                </div>

                <div className="p-6">
                    <ResidentContactForm
                        initialValue={contact}
                        submitting={submitting}
                        onCancel={onClose}
                        onSubmit={onSubmit}
                    />
                </div>
            </div>
        </div>
    );
}
