import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useActiveCnas } from "../../hooks/useCareTasks";
import { useState } from "react";
import { toast } from "sonner";

interface AssignCnaModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (cnaId: number | null) => Promise<void>;
  currentCnaId: number | null;
}

export const AssignCnaModal = ({ open, onOpenChange, onConfirm, currentCnaId }: AssignCnaModalProps) => {
  const { data: cnas, isLoading } = useActiveCnas();
  const [selectedId, setSelectedId] = useState<string>(currentCnaId ? currentCnaId.toString() : "unassigned");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      const cnaId = selectedId === "unassigned" ? null : parseInt(selectedId, 10);
      await onConfirm(cnaId);
      toast.success("Assignment updated successfully");
      onOpenChange(false);
    } catch (error) {
      toast.error("Failed to update assignment");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Assign CNA</DialogTitle>
          <DialogDescription>
            Select a Certified Nursing Assistant to assign to this task.
          </DialogDescription>
        </DialogHeader>
        <div className="py-4">
          <Select 
            value={selectedId} 
            onValueChange={(val: string | null) => setSelectedId(val || "unassigned")}
            disabled={isLoading || isSubmitting}
          >
            <SelectTrigger>
              <SelectValue placeholder="Select a CNA" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="unassigned" className="text-muted-foreground italic">
                Unassigned
              </SelectItem>
              {cnas?.map((cna: any) => (
                <SelectItem key={cna.id} value={cna.id.toString()}>
                  {cna.fullName || `${cna.firstName} ${cna.lastName}`}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}>
            Save Changes
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
