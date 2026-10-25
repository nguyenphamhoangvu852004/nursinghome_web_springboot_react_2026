import { AlertCircle, Loader2, Mail, MapPin, Phone, UserRound } from "lucide-react";
import { useEffect, useState } from "react";

import type {
    ContactDetail,
    CreateContactRequest,
} from "../types/contact.types";

interface ContactFormProps {
    initialValue?: ContactDetail | null;
    submitting?: boolean;
    onSubmit: (payload: CreateContactRequest) => Promise<void>;
    onCancel: () => void;
}

const emptyForm: CreateContactRequest = {
    firstName: "",
    middleName: "",
    lastName: "",
    phonePrimary: "",
    phoneSecondary: "",
    email: "",
    addressId: null,
};

const inputClassName =
    "h-11 w-full rounded-lg border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-50";

export default function ContactForm({
    initialValue,
    submitting = false,
    onSubmit,
    onCancel,
}: ContactFormProps) {
    const [form, setForm] = useState<CreateContactRequest>(emptyForm);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!initialValue) {
            setForm(emptyForm);
            return;
        }

        setForm({
            firstName: initialValue.firstName,
            middleName: initialValue.middleName ?? "",
            lastName: initialValue.lastName,
            phonePrimary: initialValue.phonePrimary,
            phoneSecondary: initialValue.phoneSecondary ?? "",
            email: initialValue.email ?? "",
            addressId: initialValue.addressId ?? null,
        });
    }, [initialValue]);

    const updateField = <K extends keyof CreateContactRequest>(
        field: K,
        value: CreateContactRequest[K],
    ) => setForm((current) => ({ ...current, [field]: value }));

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!form.firstName.trim() || !form.lastName.trim() || !form.phonePrimary.trim()) {
            setError("First name, last name and primary phone are required.");
            return;
        }

        const phonePattern = /^1-\d{3}-\d{3}-\d{4}$/;

        if (!phonePattern.test(form.phonePrimary.trim())) {
            setError("Primary phone must follow format 1-XXX-XXX-XXXX.");
            return;
        }

        if (form.phoneSecondary?.trim() && !phonePattern.test(form.phoneSecondary.trim())) {
            setError("Secondary phone must follow format 1-XXX-XXX-XXXX.");
            return;
        }

        setError(null);

        await onSubmit({
            firstName: form.firstName.trim(),
            middleName: form.middleName?.trim() || null,
            lastName: form.lastName.trim(),
            phonePrimary: form.phonePrimary.trim(),
            phoneSecondary: form.phoneSecondary?.trim() || null,
            email: form.email?.trim() || null,
            addressId: form.addressId ?? null,
        });
    };

    return (
        <form
            onSubmit={handleSubmit}
            className="space-y-6 rounded-2xl border border-gray-200 bg-white p-6 shadow-sm"
        >
            {error && (
                <div className="flex items-start gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
                    {error}
                </div>
            )}

            <section className="space-y-4">
                <div className="flex items-center gap-2 border-b border-gray-100 pb-3">
                    <UserRound className="h-4 w-4 text-blue-600" />
                    <h3 className="text-sm font-semibold text-gray-900">Personal information</h3>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                    <Field label="First name" required>
                        <input
                            value={form.firstName}
                            onChange={(event) => updateField("firstName", event.target.value)}
                            disabled={submitting}
                            className={inputClassName}
                        />
                    </Field>

                    <Field label="Middle name">
                        <input
                            value={form.middleName ?? ""}
                            onChange={(event) => updateField("middleName", event.target.value)}
                            disabled={submitting}
                            className={inputClassName}
                        />
                    </Field>
                </div>

                <Field label="Last name" required>
                    <input
                        value={form.lastName}
                        onChange={(event) => updateField("lastName", event.target.value)}
                        disabled={submitting}
                        className={inputClassName}
                    />
                </Field>
            </section>

            <section className="space-y-4">
                <div className="flex items-center gap-2 border-b border-gray-100 pb-3">
                    <Phone className="h-4 w-4 text-blue-600" />
                    <h3 className="text-sm font-semibold text-gray-900">Contact information</h3>
                </div>

                <div className="grid gap-4 sm:grid-cols-2">
                    <Field label="Primary phone" required>
                        <input
                            value={form.phonePrimary}
                            onChange={(event) => updateField("phonePrimary", event.target.value)}
                            placeholder="1-212-555-0101"
                            disabled={submitting}
                            className={inputClassName}
                        />
                    </Field>

                    <Field label="Secondary phone">
                        <input
                            value={form.phoneSecondary ?? ""}
                            onChange={(event) => updateField("phoneSecondary", event.target.value)}
                            placeholder="1-212-555-0102"
                            disabled={submitting}
                            className={inputClassName}
                        />
                    </Field>
                </div>

                <Field label="Email" icon={<Mail className="h-4 w-4 text-gray-400" />}>
                    <input
                        type="email"
                        value={form.email ?? ""}
                        onChange={(event) => updateField("email", event.target.value)}
                        disabled={submitting}
                        className={inputClassName}
                    />
                </Field>

                <Field label="Address ID" icon={<MapPin className="h-4 w-4 text-gray-400" />}>
                    <input
                        type="number"
                        min={1}
                        value={form.addressId ?? ""}
                        onChange={(event) =>
                            updateField(
                                "addressId",
                                event.target.value ? Number(event.target.value) : null,
                            )
                        }
                        disabled={submitting}
                        className={inputClassName}
                    />
                </Field>
            </section>

            <div className="flex justify-end gap-3 border-t border-gray-100 pt-5">
                <button
                    type="button"
                    onClick={onCancel}
                    disabled={submitting}
                    className="rounded-lg border border-gray-300 px-4 py-2.5 text-sm font-medium text-gray-700 transition hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    Cancel
                </button>

                <button
                    type="submit"
                    disabled={submitting}
                    className="inline-flex min-w-36 items-center justify-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                >
                    {submitting && <Loader2 className="h-4 w-4 animate-spin" />}
                    {submitting ? "Saving..." : initialValue ? "Update Contact" : "Create Contact"}
                </button>
            </div>
        </form>
    );
}

function Field({
    label,
    required,
    icon,
    children,
}: {
    label: string;
    required?: boolean;
    icon?: React.ReactNode;
    children: React.ReactNode;
}) {
    return (
        <div>
            <label className="mb-1.5 flex items-center gap-1.5 text-sm font-medium text-gray-700">
                {icon}
                {label}
                {required && <span className="text-red-500">*</span>}
            </label>
            {children}
        </div>
    );
}