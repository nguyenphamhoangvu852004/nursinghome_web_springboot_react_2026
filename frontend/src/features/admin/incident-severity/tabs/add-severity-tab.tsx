import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import type { IncidentSeverityResponse } from "../services/incident-severity-service";

interface AddSeverityTabProps {
  newSeverityName: string;
  newChartLockTrigger: boolean;
  newDescription: string;
  newExample: string;
  newSeverityError: string | null;
  onSeverityNameChange: (value: string) => void;
  onChartLockTriggerChange: (value: boolean) => void;
  onDescriptionChange: (value: string) => void;
  onExampleChange: (value: string) => void;
  onCreate: () => void;
}

export const AddSeverityTab = ({
  newSeverityName,
  newChartLockTrigger,
  newDescription,
  newExample,
  newSeverityError,
  onSeverityNameChange,
  onChartLockTriggerChange,
  onDescriptionChange,
  onExampleChange,
  onCreate,
}: AddSeverityTabProps) => {
  return (
    <div className="space-y-6 p-6">
      <div>
        <label className="block text-sm font-medium text-on-surface mb-2">Level name</label>
        <Input
          type="text"
          value={newSeverityName}
          onChange={(event) => onSeverityNameChange(event.target.value)}
          placeholder="e.g. Major"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-on-surface mb-2">Description</label>
        <Input
          type="text"
          value={newDescription}
          onChange={(event) => onDescriptionChange(event.target.value)}
          placeholder="e.g. A major incident that requires immediate attention"
        />
      </div>
      <div>
        <label className="block text-sm font-medium text-on-surface mb-2">Example</label>
        <Input
          type="text"
          value={newExample}
          onChange={(event) => onExampleChange(event.target.value)}
          placeholder="e.g. A critical system outage affecting multiple users"
        />
      </div>
      <div className="flex items-center gap-3">
        <input
          id="chartLockTrigger"
          type="checkbox"
          checked={newChartLockTrigger}
          onChange={(event) => onChartLockTriggerChange(event.target.checked)}
          className="h-4 w-4 rounded border-outline text-primary focus:ring-primary"
        />
        <label htmlFor="chartLockTrigger" className="text-sm text-on-surface">
          Chart lock trigger
        </label>
      </div>
      {newSeverityError ? <p className="text-sm text-destructive">{newSeverityError}</p> : null}
      <div className="flex justify-end gap-3">
        <Button variant="default" onClick={onCreate}>
          Create severity
        </Button>
      </div>
    </div>
  );
};
