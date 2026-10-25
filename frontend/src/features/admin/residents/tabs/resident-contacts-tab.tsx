import { Plus, RefreshCw, Users } from "lucide-react";
import { useMemo, useState } from "react";

import { residentContactApi } from "../api/resident-contact-api";
import ContactManagerModal from "../components/resident-contacts/contact-manager-modal";
import EditRelationshipModal from "../components/resident-contacts/edit-relationship-modal";
import RemoveContactDialog from "../components/resident-contacts/remove-contact-dialog";
import ResidentContactTable from "../components/resident-contacts/resident-contact-table";
import ResidentContactForm from "../forms/resident-contact-form";
import { useResidentContacts } from "../hooks/use-resident-contacts";

import type { ContactListItem } from "../types/contact.types";
import type {
    ResidentContact,
    ResidentContactRelationshipFormData,
    ResidentQuickContact,
} from "../types/resident-contact.types";

interface ResidentContactsTabProps {
    residentId: number;
}

export default function ResidentContactsTab({ residentId }: ResidentContactsTabProps) {
    const { contacts, loading, error, refetch } = useResidentContacts(residentId);

    const [contactManagerOpen, setContactManagerOpen] = useState(false);
    const [selectedContact, setSelectedContact] = useState<ContactListItem | null>(null);
    const [editingContact, setEditingContact] = useState<ResidentContact | null>(null);
    const [removingContact, setRemovingContact] = useState<ResidentContact | null>(null);
    const [saving, setSaving] = useState(false);
    const [deleting, setDeleting] = useState(false);
    const [feedback, setFeedback] = useState<string | null>(null);
    const [operationError, setOperationError] = useState<string | null>(null);
    const [guarantorContact, setGuarantorContact] = useState<ResidentQuickContact | null>(null);
    const [primaryQuickContact, setPrimaryQuickContact] = useState<ResidentQuickContact | null>(null);
    const [quickContactLoading, setQuickContactLoading] = useState(false);
    const [quickContactError, setQuickContactError] = useState<string | null>(null);

    const totalFinancialResponsibility = useMemo(
        () =>
            contacts.reduce(
                (total, contact) =>
                    total + Number(contact.financialResponsibilityPct ?? 0),
                0,
            ),
        [contacts],
    );

    const primaryContact = useMemo(
        () => contacts.find((contact) => contact.isPrimary),
        [contacts],
    );

    const clearMessages = () => {
        setFeedback(null);
        setOperationError(null);
    };

    const handleOpenContactManager = () => {
        clearMessages();
        setSelectedContact(null);
        setContactManagerOpen(true);
    };

    const handleSelectContact = (contact: ContactListItem) => {
        clearMessages();
        setSelectedContact(contact);
        setContactManagerOpen(false);
    };

    const handleCreate = async (
        payload: ResidentContactRelationshipFormData,
    ) => {
        if (!selectedContact) {
            setOperationError("Please select a contact first.");
            return;
        }

        const selectedContactName = selectedContact.fullName;

        try {
            setSaving(true);
            clearMessages();

            await residentContactApi.createResidentContact(residentId, {
                contactId: selectedContact.id,
                ...payload,
            });

            await refetch();

            setSelectedContact(null);
            setFeedback(`${selectedContactName} was linked successfully.`);
        } catch (error) {
            console.error("Failed to link contact:", error);
            setOperationError(
                error instanceof Error ? error.message : "Unable to link contact.",
            );
        } finally {
            setSaving(false);
        }
    };

    const handleUpdate = async (
        payload: ResidentContactRelationshipFormData,
    ) => {
        if (!editingContact) return;

        try {
            setSaving(true);
            clearMessages();

            await residentContactApi.updateResidentContact(
                residentId,
                editingContact.residentContactId,
                {
                    relationshipType: payload.relationshipType,
                    isPrimary: payload.isPrimary,
                    isEmergencyContact: payload.isEmergencyContact,
                    isGuarantor: payload.isGuarantor,
                    financialResponsibilityPct: payload.financialResponsibilityPct,
                },
            );

            await refetch();

            setEditingContact(null);
            setFeedback("Contact relationship updated successfully.");
        } catch (error) {
            console.error("Failed to update relationship:", error);
            setOperationError(
                error instanceof Error
                    ? error.message
                    : "Unable to update relationship.",
            );
        } finally {
            setSaving(false);
        }
    };

    const handleRemove = async () => {
        if (!removingContact) return;

        try {
            setDeleting(true);
            clearMessages();

            await residentContactApi.deleteResidentContact(
                residentId,
                removingContact.residentContactId,
            );

            await refetch();

            setRemovingContact(null);
            setFeedback("Contact relationship removed successfully.");
        } catch (error) {
            console.error("Failed to remove relationship:", error);
            setOperationError(
                error instanceof Error
                    ? error.message
                    : "Unable to remove relationship.",
            );
        } finally {
            setDeleting(false);
        }
    };

    const handleLoadQuickContacts = async () => {
        try {
            setQuickContactLoading(true);
            setQuickContactError(null);

            const [guarantor, primary] = await Promise.all([
                residentContactApi.getResidentGuarantorContact(residentId),
                residentContactApi.getResidentPrimaryContact(residentId),
            ]);

            setGuarantorContact(guarantor);
            setPrimaryQuickContact(primary);
        } catch (error) {
            console.error("Failed to load quick contacts:", error);

            setGuarantorContact(null);
            setPrimaryQuickContact(null);
            setQuickContactError(
                error instanceof Error
                    ? error.message
                    : "Unable to load quick contacts.",
            );
        } finally {
            setQuickContactLoading(false);
        }
    };

    return (
        <section className="space-y-5">
            <header className="flex flex-col gap-4 rounded-xl border border-gray-200 bg-white p-5 shadow-xs sm:flex-row sm:items-center sm:justify-between">
                <div className="flex items-start gap-3">
                    <div className="rounded-lg bg-blue-50 p-2 text-blue-600">
                        <Users size={21} />
                    </div>

                    <div>
                        <h2 className="text-lg font-semibold text-gray-900">Resident Contacts</h2>
                        <p className="mt-1 text-sm text-gray-500">
                            Manage relationships, primary contacts, guarantors and financial responsibility.
                        </p>
                    </div>
                </div>

                <div className="flex flex-wrap gap-2">
                    <button
                        type="button"
                        onClick={() => void handleLoadQuickContacts()}
                        disabled={quickContactLoading}
                        className="inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 px-4 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
                    >
                        {quickContactLoading ? "Loading..." : "Load Quick Contacts"}
                    </button>

                    <button
                        type="button"
                        onClick={() => void refetch()}
                        disabled={loading}
                        className="inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 px-4 py-2.5 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
                    >
                        <RefreshCw size={16} className={loading ? "animate-spin" : ""} />
                        Refresh
                    </button>

                    <button
                        type="button"
                        onClick={handleOpenContactManager}
                        className="inline-flex items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white hover:bg-blue-700"
                    >
                        <Plus size={17} />
                        Link Contact
                    </button>
                </div>
            </header>

            <div className="grid gap-4 sm:grid-cols-3">
                <div className="rounded-xl border bg-white p-4">
                    <p className="text-sm text-gray-500"> Total contacts </p>

                    <p className="mt-1 text-2xl font-semibold text-gray-900"> {contacts.length} </p>
                </div>

                <div className="rounded-xl border bg-white p-4">
                    <p className="text-sm text-gray-500"> Primary contact </p>

                    <p className="mt-1 text-base font-semibold text-gray-900">
                        {primaryContact?.fullName ?? "Not assigned"}
                    </p>
                </div>

                <div className="rounded-xl border bg-white p-4">
                    <p className="text-sm text-gray-500"> Financial responsibility </p>

                    <p
                        className={`mt-1 text-2xl font-semibold ${totalFinancialResponsibility >
                            100 ? "text-red-600" : "text-gray-900"}`}
                    >
                        {totalFinancialResponsibility}%
                    </p>

                    {totalFinancialResponsibility >
                        100 && (<p className="mt-1 text-xs text-red-600"> Total responsibility exceeds 100%. </p>
                        )}
                </div>
            </div>

            {feedback && (
                <div className="rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
                    {feedback}
                </div>
            )}

            {(error || operationError) && (
                <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {operationError ?? error}
                </div>
            )}

            {/* API 38 & 39 */}
            <div className="grid gap-4 md:grid-cols-2">
                <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-xs">
                    <h3 className="text-sm font-semibold text-gray-900">
                        Guarantor Contact
                    </h3>

                    {guarantorContact ? (
                        <div className="mt-3 space-y-1">
                            <p className="font-medium"> {guarantorContact.firstName} </p>

                            <p className="text-sm text-gray-500"> Contact ID: {guarantorContact.id} </p>

                            <span className="inline-flex rounded-full bg-green-100 px-2 py-1 text-xs font-medium text-green-700"> Guarantor </span>
                        </div>
                    ) : (
                        <p className="mt-3 text-sm text-gray-400"> Click "Load Quick Contacts" </p>
                    )}
                </div>

                <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-xs">
                    <h3 className="text-sm font-semibold text-gray-900"> Primary Contact </h3>

                    {primaryQuickContact ? (
                        <div className="mt-3 space-y-1">
                            <p className="font-medium"> {primaryQuickContact.firstName} </p>

                            <p className="text-sm text-gray-500"> Contact ID: {primaryQuickContact.id} </p>

                            <span className="inline-flex rounded-full bg-blue-100 px-2 py-1 text-xs font-medium text-blue-700"> Primary </span>
                        </div>
                    ) : (
                        <p className="mt-3 text-sm text-gray-400">
                            Click "Load Quick Contacts"
                        </p>
                    )}
                </div>
            </div>

            {quickContactError && (
                <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    {quickContactError}
                </div>
            )}

            <ResidentContactTable
                contacts={contacts}
                loading={loading}
                onEdit={(contact) => {
                    clearMessages();
                    setEditingContact(contact);
                }}
                onRemove={(contact) => {
                    clearMessages();
                    setRemovingContact(contact);
                }}
            />

            <ContactManagerModal
                open={contactManagerOpen}
                onClose={() =>
                    setContactManagerOpen(false)
                }
                onSelectContact={handleSelectContact}
            />

            {selectedContact && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
                    <div className="max-h-[90vh] w-full max-w-xl overflow-y-auto rounded-2xl bg-white p-6 shadow-xl">
                        <div className="mb-5">
                            <h2 className="text-lg font-semibold text-gray-900">
                                Link Contact to Resident
                            </h2>

                            <p className="mt-1 text-sm text-gray-500">
                                Selected contact:
                                <span className="ml-1 font-medium text-gray-700">
                                    {selectedContact.fullName}
                                </span>
                            </p>

                            <div className="mt-3 rounded-lg border border-blue-100 bg-blue-50 px-4 py-3 text-sm text-blue-700">
                                <p> Contact ID:{" "}{selectedContact.id}
                                </p>

                                <p>
                                    Phone:{" "}{selectedContact.phonePrimary}
                                </p>

                                <p>
                                    Email:{" "}{selectedContact.email ?? "—"}
                                </p>
                            </div>
                        </div>

                        <ResidentContactForm
                            submitting={saving}
                            onCancel={() => setSelectedContact(null)}
                            onSubmit={handleCreate}
                        />
                    </div>
                </div>
            )}

            <EditRelationshipModal
                contact={editingContact}
                submitting={saving}
                onClose={() => setEditingContact(null)}
                onSubmit={handleUpdate}
            />

            <RemoveContactDialog
                contact={removingContact}
                deleting={deleting}
                onCancel={() => setRemovingContact(null)}
                onConfirm={handleRemove}
            />
        </section>
    );
}