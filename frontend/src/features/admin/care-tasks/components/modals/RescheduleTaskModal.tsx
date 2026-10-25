import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { useState } from "react";

interface RescheduleTaskModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onConfirm: (dateString: string) => Promise<void>;
  currentDate: string;
}

export const RescheduleTaskModal = ({ open, onOpenChange, onConfirm, currentDate }: RescheduleTaskModalProps) => {
  // Simple native datetime-local input for MVP. 
  // Should map to ISO string for backend
  const [date, setDate] = useState(() => {
    try {
      return new Date(currentDate).toISOString().slice(0, 16);
    } catch {
      return new Date().toISOString().slice(0, 16);
    }
  });
  
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      const isoString = new Date(date).toISOString();
      await onConfirm(isoString);
      toast.success("Task rescheduled successfully");
      onOpenChange(false);
    } catch (error) {
      toast.error("Failed to reschedule task");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Reschedule Task</DialogTitle>
          <DialogDescription>
            Choose a new date and time for this task.
          </DialogDescription>
        </DialogHeader>
        <div className="py-4">
          <input 
            type="datetime-local" 
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            disabled={isSubmitting}
          />
        </div>
        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}>
            Reschedule
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
