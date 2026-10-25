import { useState, useEffect } from "react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import type { Bed } from "@/services/rooms-api";

interface BedFormProps {
  initialData?: Bed;
  onSubmit: (data: Partial<Bed>) => void;
  onCancel: () => void;
}

export const BedForm = ({ initialData, onSubmit, onCancel }: BedFormProps) => {
  const [formData, setFormData] = useState<Partial<Bed>>({
    bed_number: "",
    status: "AVAILABLE",
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        bed_number: initialData.bed_number,
        status: initialData.status,
      });
    } else {
      setFormData({
        bed_number: "",
        status: "AVAILABLE",
      });
    }
  }, [initialData]);

  const handleChange = (field: keyof Bed, value: any) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="bed_number" className="text-label-md font-label-md text-on-surface">
          Bed Number
        </Label>
        <Input
          id="bed_number"
          value={formData.bed_number}
          onChange={(e) => handleChange("bed_number", e.target.value)}
          placeholder="e.g. 101A"
          required
          className="border-outline-variant focus:border-primary"
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="status" className="text-label-md font-label-md text-on-surface">
          Status
        </Label>
        <Select
          value={formData.status}
          onValueChange={(val) => handleChange("status", val)}
        >
          <SelectTrigger id="status" className="border-outline-variant focus:ring-primary w-full">
            <SelectValue placeholder="Select status" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="AVAILABLE">AVAILABLE</SelectItem>
            <SelectItem value="OCCUPIED">OCCUPIED</SelectItem>
            <SelectItem value="MAINTENANCE">MAINTENANCE</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div className="flex justify-end gap-3 pt-4 border-t border-outline-variant/30">
        <Button
          type="button"
          variant="outline"
          onClick={onCancel}
          className="border-outline-variant text-on-surface hover:bg-surface-container-low"
        >
          Cancel
        </Button>
        <Button
          type="submit"
          className="bg-primary text-on-primary hover:bg-primary/90"
        >
          Save Bed
        </Button>
      </div>
    </form>
  );
};
