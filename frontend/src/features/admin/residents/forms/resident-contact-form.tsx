import { AlertCircle, Loader2, Percent, UsersRound } from "lucide-react";
import { type FormEvent, useEffect, useState } from "react";

import type {
    ResidentContact,
    ResidentContactRelationshipFormData,
} from "../types/resident-contact.types";

interface ResidentContactFormProps {
    initialValue?: ResidentContact | null;
    submitting?: boolean;
    onCancel: () => void;
    onSubmit: (payload: ResidentContactRelationshipFormData) => Promise<void>;
}

const relationshipOptions = [
    "SPOUSE",
    "SON",
    "DAUGHTER",
    "FATHER",
    "MOTHER",
    "BROTHER",
    "SISTER",
    "LEGAL_GUARDIAN",
    "FRIEND",
    "OTHER",
];

const createInitialForm = (
    initialValue?: ResidentContact | null,
): ResidentContactRelationshipFormData => ({
    relationshipType: initialValue?.relationshipType ?? "",
    isPrimary: initialValue?.isPrimary ?? false,
    isEmergencyContact: initialValue?.isEmergencyContact ?? false,
    isGuarantor: initialValue?.isGuarantor ?? false,
    financialResponsibilityPct: initialValue?.financialResponsibilityPct ?? 0,
});

const fieldClassName =
    "h-11 w-full rounded-lg border border-gray-300 bg-white px-3 text-sm text-gray-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-gray-50";

export default function ResidentContactForm({
    initialValue,
    submitting = false,
    onCancel,
    onSubmit,
}: ResidentContactFormProps) {
    const [form, setForm] = useState<ResidentContactRelationshipFormData>(
        createInitialForm(initialValue),
    );
    const [validationError, setValidationError] = useState<string | null>(null);

    useEffect(() => {
        setForm(createInitialForm(initialValue));
    }, [initialValue]);

    const updateField = <K extends keyof ResidentContactRelationshipFormData>(
        field: K,
        value: ResidentContactRelationshipFormData[K],
    ) => setForm((current) => ({ ...current, [field]: value }));

    const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setValidationError(null);

        if (!form.relationshipType.trim()) {
            setValidationError("Relationship type is required.");
            return;
        }

        if (
            form.financialResponsibilityPct < 0 ||
            form.financialResponsibilityPct > 100
        ) {
            setValidationError(
                "Financial responsibility must be between 0 and 100.",
            );
            return;
        }

        await onSubmit({
            relationshipType: form.relationshipType.trim(),
            isPrimary: form.isPrimary,
            isEmergencyContact: form.isEmergencyContact,
            isGuarantor: form.isGuarantor,
            financialResponsibilityPct: form.financialResponsibilityPct,
        });
    };

    return (
        <form
            onSubmit={handleSubmit}
            className="space-y-6 rounded-2xl border border-gray-200 bg-white p-6 shadow-sm"
        >
            <div className="flex items-center gap-2 border-b border-gray-100 pb-4">
                <UsersRound className="h-5 w-5 text-blue-600" />
                <div>
                    <h3 className="font-semibold text-gray-900">
                        Contact relationship
                    </h3>
                    <p className="text-sm text-gray-500">
                        Configure the contact role and financial responsibility.
                    </p>
                </div>
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
                <div>
                    <label
                        htmlFor="relationshipType"
                        className="mb-1.5 block text-sm font-medium text-gray-700"
                    >
                        Relationship type <span className="text-red-500">*</span>
                    </label>

                    <select
                        id="relationshipType"
                        value={form.relationshipType}
                        disabled={submitting}
                        onChange={(event) =>
                            updateField("relationshipType", event.target.value)
                        }
                        className={fieldClassName}
                    >
                        <option value="">Select relationship</option>
                        {relationshipOptions.map((option) => (
                            <option key={option} value={option}>
                                {option.replaceAll("_", " ")}
                            </option>
                        ))}
                    </select>
                </div>

                <div>
                    <label
                        htmlFor="financialResponsibilityPct"
                        className="mb-1.5 block text-sm font-medium text-gray-700"
                    >
                        Financial responsibility
                    </label>

                    <div className="relative">
                        <input
                            id="financialResponsibilityPct"
                            type="number"
                            min={0}
                            max={100}
                            step="0.01"
                            value={form.financialResponsibilityPct}
                            disabled={submitting}
                            onChange={(event) =>
                                updateField(
                                    "financialResponsibilityPct",
                                    Number(event.target.value),
                                )
                            }
                            className={`${fieldClassName} pr-10`}
                        />
                        <Percent className="absolute right-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
                    </div>
                </div>
            </div>

            <div className="grid gap-3 sm:grid-cols-3">
                <OptionCard
                    label="Primary"
                    description="Main contact"
                    checked={form.isPrimary}
                    disabled={submitting}
                    onChange={(value) => updateField("isPrimary", value)}
                />

                <OptionCard
                    label="Emergency"
                    description="Emergency contact"
                    checked={form.isEmergencyContact}
                    disabled={submitting}
                    onChange={(value) => updateField("isEmergencyContact", value)}
                />

                <OptionCard
                    label="Guarantor"
                    description="Financial guarantor"
                    checked={form.isGuarantor}
                    disabled={submitting}
                    onChange={(value) => updateField("isGuarantor", value)}
                />
            </div>

            {validationError && (
                <div className="flex items-start gap-2 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                    <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
                    {validationError}
                </div>
            )}

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
                    {submitting
                        ? "Saving..."
                        : initialValue
                            ? "Update relationship"
                            : "Link contact"}
                </button>
            </div>
        </form>
    );
}

interface OptionCardProps {
    label: string;
    description: string;
    checked: boolean;
    disabled: boolean;
    onChange: (value: boolean) => void;
}

function OptionCard({
    label,
    description,
    checked,
    disabled,
    onChange,
}: OptionCardProps) {
    return (
        <label
            className={`flex cursor-pointer items-start gap-3 rounded-xl border p-4 transition ${checked
                    ? "border-blue-300 bg-blue-50"
                    : "border-gray-200 hover:border-gray-300 hover:bg-gray-50"
                }`}
        >
            <input
                type="checkbox"
                checked={checked}
                disabled={disabled}
                onChange={(event) => onChange(event.target.checked)}
                className="mt-0.5 h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
            />

            <span>
                <span className="block text-sm font-medium text-gray-900">{label}</span>
                <span className="block text-xs text-gray-500">{description}</span>
            </span>
        </label>
    );
}