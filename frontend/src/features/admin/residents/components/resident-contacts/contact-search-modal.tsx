import { AlertCircle, Loader2, Search, UserRoundSearch, X } from "lucide-react";

import { useContacts } from "../../hooks/use-contacts";
import type { ContactListItem } from "../../types/contact.types";

interface ContactSearchModalProps {
    open: boolean;
    onClose: () => void;
    onSelect: (contact: ContactListItem) => void;
    onCreateNew: () => void;
}

export default function ContactSearchModal({
    open,
    onClose,
    onSelect,
    onCreateNew,
}: ContactSearchModalProps) {
    const {
        contacts,
        search,
        loading,
        error,
        setSearch,
    } = useContacts(open);

    if (!open) {
        return null;
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-[2px]">
            <div className="flex max-h-[85vh] w-full max-w-3xl flex-col overflow-hidden rounded-2xl bg-white shadow-xl">
                <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900">
                            Select existing contact
                        </h2>
                        <p className="text-sm text-gray-500">
                            Search by name, phone number or email.
                        </p>
                    </div>

                    <button
                        type="button"
                        onClick={onClose}
                        className="rounded-lg p-2 text-gray-500 transition-colors hover:bg-gray-100 hover:text-gray-700"
                        aria-label="Close"
                    >
                        <X size={20} />
                    </button>
                </div>

                <div className="border-b border-gray-200 p-5">
                    <div className="relative">
                        <Search
                            size={18}
                            className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
                        />

                        <input
                            value={search}
                            onChange={(event) => setSearch(event.target.value)}
                            placeholder="Search contacts..."
                            autoFocus
                            className="h-11 w-full rounded-lg border border-gray-300 pl-10 pr-3 text-sm outline-none transition-colors focus:border-blue-400 focus:ring-2 focus:ring-blue-100"
                        />
                    </div>
                </div>

                <div className="flex-1 overflow-y-auto p-5">
                    {loading && (
                        <div className="flex items-center justify-center gap-2 py-10 text-sm text-gray-500">
                            <Loader2 className="h-4 w-4 animate-spin" />
                            Loading contacts...
                        </div>
                    )}

                    {error && (
                        <div className="flex items-start gap-2.5 rounded-lg border border-red-200 bg-red-50 p-3 text-sm text-red-800">
                            <AlertCircle className="mt-0.5 h-4 w-4 shrink-0 text-red-600" />
                            {error}
                        </div>
                    )}

                    {!loading && !error && contacts.length === 0 && (
                        <div className="flex flex-col items-center gap-2 py-10 text-center text-sm text-gray-500">
                            <UserRoundSearch className="h-8 w-8 text-gray-300" />
                            No contacts found.
                        </div>
                    )}

                    {!loading && !error && contacts.length > 0 && (
                        <div className="space-y-3">
                            {contacts.map((contact) => (
                                <button
                                    key={contact.id}
                                    type="button"
                                    onClick={() => onSelect(contact)}
                                    className="flex w-full items-center justify-between rounded-xl border border-gray-200 p-4 text-left transition-colors hover:border-blue-300 hover:bg-blue-50"
                                >
                                    <div>
                                        <p className="font-medium text-gray-900">
                                            {contact.fullName}
                                        </p>

                                        <p className="mt-1 text-sm text-gray-500">
                                            {contact.phonePrimary}
                                            {contact.email ? ` · ${contact.email}` : ""}
                                        </p>
                                    </div>

                                    <span className="shrink-0 text-sm font-medium text-blue-600">
                                        Select
                                    </span>
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                <div className="flex items-center justify-between border-t border-gray-200 px-6 py-4">
                    <p className="text-sm text-gray-500">
                        Cannot find the contact?
                    </p>

                    <button
                        type="button"
                        onClick={onCreateNew}
                        className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-blue-700"
                    >
                        Create new contact
                    </button>
                </div>
            </div>
        </div>
    );
}
