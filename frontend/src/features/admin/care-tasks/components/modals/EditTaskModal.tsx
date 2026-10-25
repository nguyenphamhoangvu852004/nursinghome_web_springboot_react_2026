import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter, DialogDescription } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useActiveCnas } from "../../hooks/useCareTasks";
import { useState, useEffect } from "react";
import { toast } from "sonner";
import type { EnrichedTaskRow } from "@/services/care-tasks-api";

interface EditTaskModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  task: EnrichedTaskRow | null;
  onConfirm: (payload: { taskType?: string; assignedCnaId?: number | null; scheduledTime?: string; goal?: string }) => Promise<void>;
}

export const EditTaskModal = ({ open, onOpenChange, task, onConfirm }: EditTaskModalProps) => {
  const { data: cnas, isLoading } = useActiveCnas();
  
  const [taskType, setTaskType] = useState("");
  const [goal, setGoal] = useState("");
  const [selectedCnaId, setSelectedCnaId] = useState<string>("unassigned");
  const [scheduledTime, setScheduledTime] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (task && open) {
      setTaskType(task.taskType || "");
      setGoal(task.goal || "");
      setSelectedCnaId(task.assignedCnaId ? task.assignedCnaId.toString() : "unassigned");
      try {
        setScheduledTime(new Date(task.scheduledTime).toISOString().slice(0, 16));
      } catch {
        setScheduledTime("");
      }
    }
  }, [task, open]);

  if (!task) return null;

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      const cnaId = selectedCnaId === "unassigned" ? null : parseInt(selectedCnaId, 10);
      const isoString = new Date(scheduledTime).toISOString();
      
      await onConfirm({
        taskType: taskType.trim() !== "" ? taskType : undefined,
        goal: goal.trim() !== "" ? goal : undefined,
        assignedCnaId: cnaId,
        scheduledTime: isoString,
      });
      
      toast.success("Task updated successfully");
      onOpenChange(false);
    } catch (error) {
      toast.error("Failed to update task");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Edit Task</DialogTitle>
          <DialogDescription>
            Update the task details below.
          </DialogDescription>
        </DialogHeader>
        <div className="py-4 flex flex-col gap-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">Task Type</label>
            <Input 
              value={taskType} 
              onChange={(e) => setTaskType(e.target.value)} 
              disabled={isSubmitting}
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-medium">Goal / Notes</label>
            <Textarea 
              value={goal} 
              onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => setGoal(e.target.value)} 
              disabled={isSubmitting}
              className="resize-none"
              rows={3}
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-medium">Assigned CNA</label>
            <Select 
              value={selectedCnaId} 
              onValueChange={(val) => setSelectedCnaId(val || "unassigned")}
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
          <div className="space-y-2">
            <label className="text-sm font-medium">Scheduled Time</label>
            <input 
              type="datetime-local" 
              className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
              value={scheduledTime}
              onChange={(e) => setScheduledTime(e.target.value)}
              disabled={isSubmitting}
            />
          </div>
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
