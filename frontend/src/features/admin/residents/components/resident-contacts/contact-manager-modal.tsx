import {
    AlertCircle,
    CheckCircle2,
    ChevronLeft,
    ChevronRight,
    Edit,
    Eye,
    Loader2,
    Plus,
    Search,
    Trash2,
    Users,
    X,
} from "lucide-react";
import { useCallback, useEffect, useState } from "react";

import { contactApi } from "../../api/contact-api";
import ContactForm from "../../forms/contact-form";
import DeleteContactDialog from "./delete-contact-dialog";

import type {
    ContactDetail,
    ContactListItem,
    CreateContactRequest,
    ResidentByContact,
    UpdateContactRequest,
} from "../../types/contact.types";

interface ContactManagerModalProps {
    open: boolean;
    onClose: () => void;
    onSelectContact?: (contact: ContactListItem) => void;
}

type ModalMode = "list" | "detail" | "create" | "edit" | "residents";

const statusBadgeClassName = (isDeleted: boolean) =>
    isDeleted
        ? "border border-red-200 bg-red-50 text-red-700"
        : "border border-emerald-200 bg-emerald-50 text-emerald-700";

export default function ContactManagerModal({
    open,
    onClose,
    onSelectContact,
}: ContactManagerModalProps) {
    const [mode, setMode] = useState<ModalMode>("list");
    const [contacts, setContacts] = useState<ContactListItem[]>([]);
    const [selectedContact, setSelectedContact] = useState<ContactDetail | null>(null);
    const [linkedResidents, setLinkedResidents] = useState<ResidentByContact[]>([]);
    const [search, setSearch] = useState("");
    const [includeDeleted, setIncludeDeleted] = useState(false);
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);
    const [total, setTotal] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [deletingContact, setDeletingContact] = useState<ContactListItem | null>(null);
    const [deleting, setDeleting] = useState(false);

    const fetchContacts = useCallback(async () => {
        if (!open) return;

        try {
            setLoading(true);
            setError(null);

            const result = await contactApi.getContacts({
                search,
                includeDeleted,
                page,
                pageSize,
            });

            setContacts(result.contacts);
            setTotal(result.meta.total);
            setTotalPages(result.meta.totalPages);
        } catch (error) {
            setContacts([]);
            setError(error instanceof Error ? error.message : "Unable to load contacts.");
        } finally {
            setLoading(false);
        }
    }, [open, search, includeDeleted, page, pageSize]);

    useEffect(() => {
        if (!open) return;

        const timeoutId = window.setTimeout(() => void fetchContacts(), 300);
        return () => window.clearTimeout(timeoutId);
    }, [open, fetchContacts]);

    useEffect(() => {
        if (!open) return;

        setMode("list");
        setSelectedContact(null);
        setLinkedResidents([]);
        setMessage(null);
        setError(null);
    }, [open]);

    const handleViewDetail = async (contactId: number) => {
        try {
            setLoading(true);
            setError(null);

            const detail = await contactApi.getContactById(contactId);
            setSelectedContact(detail);
            setMode("detail");
        } catch (error) {
            setError(
                error instanceof Error ? error.message : "Unable to load contact detail.",
            );
        } finally {
            setLoading(false);
        }
    };

    const handleEdit = async (contactId: number) => {
        try {
            setLoading(true);
            setError(null);

            const detail = await contactApi.getContactById(contactId);
            setSelectedContact(detail);
            setMode("edit");
        } catch (error) {
            setError(error instanceof Error ? error.message : "Unable to load contact.");
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async (payload: CreateContactRequest) => {
        try {
            setSubmitting(true);
            setError(null);

            const created = await contactApi.createContact(payload);
            setMessage(`Created contact ${created.fullName} successfully.`);
            setMode("list");

            await fetchContacts();
        } catch (error) {
            setError(error instanceof Error ? error.message : "Unable to create contact.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleUpdate = async (payload: UpdateContactRequest) => {
        if (!selectedContact) return;

        try {
            setSubmitting(true);
            setError(null);
            setMessage(null);

            const updated = await contactApi.updateContact(selectedContact.id, payload);
            setSelectedContact(updated);
            setMessage("Updated contact successfully.");
            setMode("list");

            await fetchContacts();
        } catch (error) {
            setError(error instanceof Error ? error.message : "Unable to update contact.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleDelete = async () => {
        if (!deletingContact) return;

        const contactName = deletingContact.fullName;

        try {
            setDeleting(true);
            setError(null);
            setMessage(null);

            await contactApi.deleteContact(deletingContact.id);

            setDeletingContact(null);
            setMessage(`Contact "${contactName}" was deleted successfully.`);

            await fetchContacts();
        } catch (error) {
            console.error("Failed to delete contact:", error);
            setError(error instanceof Error ? error.message : "Unable to delete contact.");
        } finally {
            setDeleting(false);
        }
    };

    const handleViewResidents = async (contactId: number) => {
        try {
            setLoading(true);
            setError(null);

            const detail = await contactApi.getContactById(contactId);
            const residents = await contactApi.getResidentsByContact(contactId);

            setSelectedContact(detail);
            setLinkedResidents(residents);
            setMode("residents");
        } catch (error) {
            setError(
                error instanceof Error ? error.message : "Unable to load linked residents.",
            );
        } finally {
            setLoading(false);
        }
    };

    if (!open) return null;

    return (
        <>
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 backdrop-blur-[2px]">
                <div className="flex max-h-[90vh] w-full max-w-5xl flex-col overflow-hidden rounded-2xl bg-white shadow-xl">
                    <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4">
                        <div>
                            <h2 className="text-lg font-semibold text-gray-900">
                                Contact Management
                            </h2>
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

                    <div className="flex-1 overflow-y-auto p-6">
                        {message && (
                            <div className="mb-4 flex items-start gap-2.5 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-800">
                                <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-emerald-600" />
                                {message}
                            </div>
                        )}

                        {error && (
                            <div className="mb-4 flex items-start gap-2.5 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
                                <AlertCircle className="mt-0.5 h-4 w-4 shrink-0 text-red-600" />
                                {error}
                            </div>
                        )}

                        {mode === "list" && (
                            <div className="space-y-5">
                                <div className="flex flex-col gap-3 sm:flex-row">
                                    <div className="relative flex-1">
                                        <Search
                                            size={18}
                                            className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
                                        />

                                        <input
                                            value={search}
                                            onChange={(event) => {
                                                setSearch(event.target.value);
                                                setPage(1);
                                            }}
                                            placeholder="Search name, phone or email"
                                            className="h-11 w-full rounded-lg border border-gray-300 pl-10 pr-3 text-sm outline-none transition-colors focus:border-blue-400 focus:ring-2 focus:ring-blue-100"
                                        />
                                    </div>

                                    <label className="flex h-11 cursor-pointer items-center gap-2 rounded-lg border border-gray-300 px-4 text-sm text-gray-700 transition-colors hover:bg-gray-50">
                                        <input
                                            type="checkbox"
                                            checked={includeDeleted}
                                            onChange={(event) => {
                                                setIncludeDeleted(event.target.checked);
                                                setPage(1);
                                            }}
                                            className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-400"
                                        />
                                        Include deleted
                                    </label>

                                    <button
                                        type="button"
                                        onClick={() => {
                                            setSelectedContact(null);
                                            setMode("create");
                                        }}
                                        className="inline-flex h-11 items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 text-sm font-medium text-white transition-colors hover:bg-blue-700"
                                    >
                                        <Plus size={17} />
                                        Create Contact
                                    </button>
                                </div>

                                <div className="overflow-hidden rounded-xl border border-gray-200 shadow-sm">
                                    <div className="overflow-x-auto">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                                <tr>
                                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Contact
                                                    </th>
                                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Status
                                                    </th>
                                                    <th className="px-4 py-3 text-right text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Actions
                                                    </th>
                                                </tr>
                                            </thead>

                                            <tbody className="divide-y divide-gray-100">
                                                {loading ? (
                                                    <tr>
                                                        <td colSpan={3} className="px-4 py-10">
                                                            <div className="flex items-center justify-center gap-2 text-sm text-gray-500">
                                                                <Loader2 className="h-4 w-4 animate-spin" />
                                                                Loading...
                                                            </div>
                                                        </td>
                                                    </tr>
                                                ) : contacts.length === 0 ? (
                                                    <tr>
                                                        <td colSpan={3} className="px-4 py-10 text-center text-sm text-gray-500">
                                                            No contacts found.
                                                        </td>
                                                    </tr>
                                                ) : (
                                                    contacts.map(contact => (
                                                        <tr key={contact.id} className="transition-colors hover:bg-gray-50">
                                                            <td className="px-4 py-4">
                                                                <p className="font-medium text-gray-900">{contact.fullName}</p>
                                                                <p className="mt-1 text-sm text-gray-500">{contact.phonePrimary}</p>
                                                                <p className="text-sm text-gray-500">{contact.email ?? "—"}</p>
                                                            </td>

                                                            <td className="px-4 py-4">
                                                                <span
                                                                    className={`inline-flex rounded-full px-2.5 py-1 text-xs font-medium ${statusBadgeClassName(contact.isDeleted)}`}
                                                                >
                                                                    {contact.isDeleted ? "Deleted" : "Active"}
                                                                </span>
                                                            </td>

                                                            <td className="px-4 py-4">
                                                                <div className="flex justify-end gap-2">
                                                                    {onSelectContact && !contact.isDeleted && (
                                                                        <button
                                                                            type="button"
                                                                            onClick={() => onSelectContact(contact)}
                                                                            className="rounded-lg bg-blue-600 px-3 py-1.5 text-sm font-medium text-white transition-colors hover:bg-blue-700"
                                                                        >
                                                                            Select
                                                                        </button>
                                                                    )}

                                                                    <button
                                                                        type="button"
                                                                        onClick={() => void handleViewDetail(contact.id)}
                                                                        className="rounded-lg border border-gray-300 p-2 text-gray-600 transition-colors hover:border-gray-400 hover:bg-gray-50"
                                                                        title="View detail"
                                                                    >
                                                                        <Eye size={16} />
                                                                    </button>

                                                                    <button
                                                                        type="button"
                                                                        onClick={() => void handleEdit(contact.id)}
                                                                        disabled={contact.isDeleted}
                                                                        className="rounded-lg border border-gray-300 p-2 text-gray-600 transition-colors hover:border-gray-400 hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:border-gray-300 disabled:hover:bg-transparent"
                                                                        title="Edit contact"
                                                                    >
                                                                        <Edit size={16} />
                                                                    </button>

                                                                    <button
                                                                        type="button"
                                                                        onClick={() => void handleViewResidents(contact.id)}
                                                                        className="rounded-lg border border-gray-300 p-2 text-gray-600 transition-colors hover:border-gray-400 hover:bg-gray-50"
                                                                        title="Linked residents"
                                                                    >
                                                                        <Users size={16} />
                                                                    </button>

                                                                    <button
                                                                        type="button"
                                                                        onClick={() => setDeletingContact(contact)}
                                                                        disabled={contact.isDeleted}
                                                                        title="Delete contact"
                                                                        className="rounded-lg border border-red-200 p-2 text-red-600 transition-colors hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-transparent"
                                                                    >
                                                                        <Trash2 size={16} />
                                                                    </button>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    ))
                                                )}
                                            </tbody>
                                        </table>
                                    </div>

                                    <div className="flex flex-col gap-3 border-t border-gray-200 bg-gray-50 px-4 py-3 sm:flex-row sm:items-center sm:justify-between">
                                        <p className="text-sm text-gray-500">Total: {total}</p>

                                        <div className="flex items-center gap-2">
                                            <select
                                                value={pageSize}
                                                onChange={(event) => {
                                                    setPageSize(Number(event.target.value));
                                                    setPage(1);
                                                }}
                                                className="rounded-lg border border-gray-300 bg-white px-2 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-50"
                                            >
                                                <option value={5}>5 / page</option>
                                                <option value={10}>10 / page</option>
                                                <option value={20}>20 / page</option>
                                            </select>

                                            <button
                                                type="button"
                                                disabled={page <= 1}
                                                onClick={() => setPage(current => current - 1)}
                                                className="flex items-center gap-1 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-white"
                                            >
                                                <ChevronLeft size={14} />
                                                Previous
                                            </button>

                                            <span className="text-sm text-gray-600">Page {page} / {Math.max(totalPages, 1)}</span>

                                            <button
                                                type="button"
                                                disabled={page >= totalPages}
                                                onClick={() => setPage(current => current + 1)}
                                                className="flex items-center gap-1 rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:bg-white"
                                            >
                                                Next
                                                <ChevronRight size={14} />
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        )}

                        {mode === "create" && (
                            <div className="mx-auto max-w-2xl">
                                <h3 className="mb-5 text-lg font-semibold text-gray-900"> Create Contact </h3>

                                <ContactForm
                                    submitting={submitting}
                                    onSubmit={handleCreate}
                                    onCancel={() =>
                                        setMode("list")
                                    }
                                />
                            </div>
                        )}

                        {mode === "edit" &&
                            selectedContact && (
                                <div className="mx-auto max-w-2xl">
                                    <h3 className="mb-5 text-lg font-semibold text-gray-900"> Edit Contact </h3>

                                    <ContactForm
                                        initialValue={selectedContact}
                                        submitting={submitting}
                                        onSubmit={handleUpdate}
                                        onCancel={() => setMode("list")}
                                    />
                                </div>
                            )}

                        {mode === "detail" && selectedContact && (
                            <div className="mx-auto max-w-2xl space-y-5">
                                <div className="flex items-center justify-between">
                                    <h3 className="text-lg font-semibold text-gray-900">Contact Detail</h3>

                                    <button
                                        type="button"
                                        onClick={() => setMode("list")}
                                        className="rounded-lg border border-gray-300 px-3 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-50"
                                    >
                                        Back
                                    </button>
                                </div>

                                <div className="grid gap-5 rounded-xl border border-gray-200 bg-white p-5 shadow-sm sm:grid-cols-2">
                                    <Info label="Name" value={selectedContact.fullName} />
                                    <Info label="Primary phone" value={selectedContact.phonePrimary} />
                                    <Info label="Secondary phone" value={selectedContact.phoneSecondary} />
                                    <Info label="Email" value={selectedContact.email} />
                                    <Info
                                        label="Address"
                                        value={[
                                            selectedContact.streetLine1,
                                            selectedContact.streetLine2,
                                            selectedContact.city,
                                            selectedContact.state,
                                            selectedContact.zipCode,
                                        ]
                                            .filter(Boolean)
                                            .join(", ")}
                                    />
                                    <Info label="Status" value={selectedContact.isDeleted ? "Deleted" : "Active"} />
                                    <Info label="Created" value={selectedContact.createdAt} />
                                    <Info label="Updated" value={selectedContact.updatedAt} />
                                </div>
                            </div>
                        )}

                        {mode === "residents" && selectedContact && (
                            <div className="space-y-5">
                                <div className="flex items-center justify-between">
                                    <div>
                                        <h3 className="text-lg font-semibold text-gray-900">Linked Residents</h3>
                                        <p className="text-sm text-gray-500">{selectedContact.fullName}</p>
                                    </div>

                                    <button
                                        type="button"
                                        onClick={() => setMode("list")}
                                        className="rounded-lg border border-gray-300 px-3 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-50"
                                    >
                                        Back
                                    </button>
                                </div>

                                {linkedResidents.length === 0 ? (
                                    <div className="rounded-xl border border-dashed border-gray-300 bg-gray-50 p-10 text-center text-sm text-gray-500">
                                        This contact is not linked to any resident.
                                    </div>
                                ) : (
                                    <div className="overflow-hidden rounded-xl border border-gray-200 shadow-sm">
                                        <table className="min-w-full divide-y divide-gray-200">
                                            <thead className="bg-gray-50">
                                                <tr>
                                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Resident
                                                    </th>
                                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Relationship
                                                    </th>
                                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Status
                                                    </th>
                                                    <th className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-gray-500">
                                                        Roles
                                                    </th>
                                                </tr>
                                            </thead>

                                            <tbody className="divide-y divide-gray-100">
                                                {linkedResidents.map((resident) => (
                                                    <tr
                                                        key={resident.residentId}
                                                        className="transition-colors hover:bg-gray-50"
                                                    >
                                                        <td className="px-4 py-4 text-sm font-medium text-gray-900">
                                                            {resident.residentName}
                                                        </td>
                                                        <td className="px-4 py-4 text-sm text-gray-700">
                                                            {resident.relationshipType}
                                                        </td>
                                                        <td className="px-4 py-4 text-sm text-gray-700">
                                                            {resident.residentStatus}
                                                        </td>
                                                        <td className="px-4 py-4 text-sm text-gray-700">
                                                            {[
                                                                resident.isPrimary && "Primary",
                                                                resident.isEmergencyContact && "Emergency",
                                                                resident.isGuarantor && "Guarantor",
                                                            ]
                                                                .filter(Boolean)
                                                                .join(", ") || "—"}
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>

                <DeleteContactDialog
                    open={!!deletingContact}
                    contact={deletingContact}
                    deleting={deleting}
                    onCancel={() => setDeletingContact(null)}
                    onConfirm={() => void handleDelete()}
                />

            </div>
        </>


    );
}

function Info({ label, value }: { label: string; value?: string | null }) {
    return (
        <div>
            <p className="text-xs font-medium uppercase tracking-wide text-gray-500">{label}</p>
            <p className="mt-1 break-words text-sm text-gray-900">{value || "—"}</p>
        </div>
    );
}