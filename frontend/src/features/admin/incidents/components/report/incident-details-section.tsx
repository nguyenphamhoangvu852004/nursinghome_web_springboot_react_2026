"use client";

import * as React from "react";

import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import {
  incidentSeverityApi,
  type SeverityLevel,
} from "@/services/incidents/incidents-severity-api";
import {
  residentService,
  type ResidentListItemFE,
} from "@/services/resident/residentService";

const inputClass =
  "h-10 w-full rounded-md border border-gray-300 bg-white px-3 text-sm focus:border-primary focus:ring-2 focus:ring-primary/20";

const inputStyles =
  "h-10 w-full rounded-lg border border-input bg-transparent px-3 text-sm outline-none transition-colors placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50";

function Textarea(
  props: React.TextareaHTMLAttributes<HTMLTextAreaElement>
) {
  return (
    <textarea
      {...props}
      className={`${inputStyles} min-h-[104px] resize-none py-2`}
    />
  );
}

function Field({
  label,
  required,
  children,
}: {
  label: string;
  required?: boolean;
  children: React.ReactNode;
}) {
  return (
    <div className="space-y-2">
      <Label className="text-sm font-medium text-foreground">
        {label}
        {required && <span className="ml-1 text-destructive">*</span>}
      </Label>
      {children}
    </div>
  );
}

const incidentTypeOptions = [
  { value: "FALL", label: "Fall" },
  { value: "MEDICATION_ERROR", label: "Medication Error" },
  { value: "ALTERCATION", label: "Altercation" },
  { value: "SKIN_TEAR", label: "Skin Tear" },
];

export type IncidentFormValues = {
  residentId?: number;
  residentLabel: string;
  incidentType: string;
  severityId?: number;
  dateTime: string;
  location: string;
  description: string;
  witnesses: string;
};

export type IncidentDetailsData = {
  residentId?: number;
  resident?: string;
  incidentType?: string;
  severityId?: number;
  dateTime?: string;
  location?: string;
  description?: string;
  witnesses?: string;
  immediateAction?: string;
};

/**
 * Search/select combobox for picking a resident.
 * - Debounces the query and calls residentService.getResidents(search).
 * - Selecting a suggestion sets both the visible label and the residentId.
 * - Editing the text after a selection clears residentId so the form
 *   still requires an explicit pick (keeps existing validation working).
 */
function ResidentCombobox({
  value,
  selectedId,
  onChangeText,
  onSelect,
}: {
  value: string;
  selectedId?: number;
  onChangeText: (text: string) => void;
  onSelect: (resident: ResidentListItemFE) => void;
}) {
  const [isOpen, setIsOpen] = React.useState(false);
  const [isLoading, setIsLoading] = React.useState(false);
  const [results, setResults] = React.useState<ResidentListItemFE[]>([]);
  const [error, setError] = React.useState<string | null>(null);

  const containerRef = React.useRef<HTMLDivElement>(null);
  const debounceRef = React.useRef<ReturnType<typeof setTimeout> | null>(null);
  const requestIdRef = React.useRef(0);

  // Close dropdown when clicking outside of it.
  React.useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Debounced search whenever the text changes and there's no confirmed
  // selection matching it (i.e. user is actively typing/searching).
  React.useEffect(() => {
    if (debounceRef.current) clearTimeout(debounceRef.current);

    const query = value.trim();

    if (!query) {
      setResults([]);
      setIsOpen(false);
      setIsLoading(false);
      return;
    }

    debounceRef.current = setTimeout(async () => {
      const currentRequestId = ++requestIdRef.current;
      setIsLoading(true);
      setError(null);
      try {
        const list = await residentService.getResidents(query);
        // Ignore stale responses from older requests.
        if (currentRequestId !== requestIdRef.current) return;
        setResults(list);
        setIsOpen(true);
      } catch (e: any) {
        if (currentRequestId !== requestIdRef.current) return;
        setError(e?.response?.data?.message ?? e?.message ?? "Search failed");
        setResults([]);
      } finally {
        if (currentRequestId === requestIdRef.current) setIsLoading(false);
      }
    }, 300);

    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [value]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onChangeText(e.target.value);
  };

  const handleFocus = () => {
    if (results.length > 0) setIsOpen(true);
  };

  const handleSelect = (resident: ResidentListItemFE) => {
    onSelect(resident);
    setIsOpen(false);
    setResults([]);
  };

  return (
    <div ref={containerRef} className="relative">
      <Input
        className={inputClass}
        value={value}
        onChange={handleInputChange}
        onFocus={handleFocus}
        placeholder="Search and select resident"
        autoComplete="off"
      />

      {selectedId !== undefined && (
        <p className="mt-1 text-xs text-emerald-600">
          Selected resident ID: {selectedId}
        </p>
      )}

      {isOpen && (
        <div className="absolute z-10 mt-1 max-h-64 w-full overflow-auto rounded-md border border-gray-200 bg-white shadow-lg">
          {isLoading && (
            <div className="px-3 py-2 text-sm text-muted-foreground">
              Searching...
            </div>
          )}

          {!isLoading && error && (
            <div className="px-3 py-2 text-sm text-destructive">{error}</div>
          )}

          {!isLoading && !error && results.length === 0 && (
            <div className="px-3 py-2 text-sm text-muted-foreground">
              No residents found.
            </div>
          )}

          {!isLoading &&
            !error &&
            results.map((resident) => (
              <button
                key={resident.id}
                type="button"
                className="flex w-full flex-col items-start px-3 py-2 text-left text-sm hover:bg-gray-50"
                onClick={() => handleSelect(resident)}
              >
                <span className="font-medium text-foreground">
                  {resident.name}
                </span>
                <span className="text-xs text-muted-foreground">
                  {resident.room} · {resident.status}
                </span>
              </button>
            ))}
        </div>
      )}
    </div>
  );
}

export function IncidentDetailsSection({
  mode = "edit",
  data,
  onChange,
}: {
  mode?: "edit" | "view";
  data?: IncidentDetailsData;
  onChange?: (values: IncidentFormValues) => void;
}) {
  const [residentLabel, setResidentLabel] = React.useState(
    data?.resident ?? ""
  );
  const [residentId, setResidentId] = React.useState<number | undefined>(
    data?.residentId
  );
  const [incidentType, setIncidentType] = React.useState(
    data?.incidentType ?? "FALL"
  );
  const [severityId, setSeverityId] = React.useState<number | undefined>(
    data?.severityId
  );
  const [dateTime, setDateTime] = React.useState(
    data?.dateTime ?? ""
  );
  const [location, setLocation] = React.useState(
    data?.location ?? ""
  );
  const [description, setDescription] = React.useState(
    data?.description ?? ""
  );
  const [witnesses, setWitnesses] = React.useState(
    data?.witnesses ?? ""
  );
  const [immediateAction, setImmediateAction] = React.useState(
    data?.immediateAction ?? ""
  );

  const [severities, setSeverities] = React.useState<SeverityLevel[]>([]);
  const [severitiesError, setSeveritiesError] = React.useState<string | null>(
    null
  );

  React.useEffect(() => {
    incidentSeverityApi
      .getAll()
      .then((list) => {
        setSeverities(list);
        if (severityId === undefined && list.length > 0) {
          setSeverityId(list[0].id);
        }
      })
      .catch((e) =>
        setSeveritiesError(e?.response?.data?.message ?? e.message)
      );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  React.useEffect(() => {
    onChange?.({
      residentId,
      residentLabel,
      incidentType,
      severityId,
      dateTime,
      location,
      description,
      witnesses,
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    residentId,
    residentLabel,
    incidentType,
    severityId,
    dateTime,
    location,
    description,
    witnesses,
  ]);

  // User is typing/editing the resident text: update the label and clear
  // any previous confirmed selection so validation forces re-selecting.
  const handleResidentTextChange = (text: string) => {
    setResidentLabel(text);
    setResidentId(undefined);
  };

  const handleResidentSelect = (resident: ResidentListItemFE) => {
    setResidentLabel(resident.name);
    setResidentId(resident.id);
  };

  return (
    <div className="rounded-[20px] border border-border bg-card p-6 shadow-sm">
      <div className="mb-6 flex items-center justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold text-foreground">
            Incident Details
          </h2>
        </div>
      </div>

      <div className="space-y-5">
        <div className="grid gap-4 md:grid-cols-2">
          <Field label="Resident" required>
            <ResidentCombobox
              value={residentLabel}
              selectedId={residentId}
              onChangeText={handleResidentTextChange}
              onSelect={handleResidentSelect}
            />
          </Field>

          <Field label="Incident Type" required>
            <select
              className={inputClass}
              value={incidentType}
              onChange={(e) => setIncidentType(e.target.value)}
            >
              {incidentTypeOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
          </Field>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Field label="Severity" required>
            <select
              className={inputClass}
              value={severityId ?? ""}
              onChange={(e) =>
                setSeverityId(
                  e.target.value ? Number(e.target.value) : undefined
                )
              }
              disabled={severities.length === 0}
            >
              {severities.length === 0 && (
                <option value="">Loading...</option>
              )}
              {severities.map((severity) => (
                <option key={severity.id} value={severity.id}>
                  {severity.level_name}
                </option>
              ))}
            </select>

            {severitiesError && (
              <p className="text-xs text-destructive">
                Failed to load severity list: {severitiesError}
              </p>
            )}
          </Field>

          <Field label="Date / Time of Incident" required>
            <Input
              type="datetime-local"
              className={inputClass}
              value={dateTime}
              onChange={(e) => setDateTime(e.target.value)}
            />
          </Field>
        </div>

        <Field label="Location" required>
          <Input
            className={inputClass}
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            placeholder="Enter incident location"
          />
        </Field>

        <Field label="Description" required>
          <Textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Describe what happened"
          />
        </Field>

        <Field label="Witnesses (optional)">
          <Textarea
            value={witnesses}
            onChange={(e) => setWitnesses(e.target.value)}
            placeholder="Enter witness names (optional)"
          />
        </Field>

        <div className="flex items-center justify-end gap-3 pt-2">
          <Button variant="outline">Cancel</Button>
        </div>
      </div>
    </div>
  );
}