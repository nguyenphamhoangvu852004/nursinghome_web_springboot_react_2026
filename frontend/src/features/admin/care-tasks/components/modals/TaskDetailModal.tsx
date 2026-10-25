import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import type { EnrichedTaskRow } from "@/services/care-tasks-api";
import { format } from "date-fns";

interface TaskDetailModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  task: EnrichedTaskRow | null;
}

export const TaskDetailModal = ({ open, onOpenChange, task }: TaskDetailModalProps) => {
  if (!task) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Task Details</DialogTitle>
        </DialogHeader>
        <div className="flex flex-col gap-4 py-4 text-sm">
          <div className="grid grid-cols-3 gap-2">
            <span className="text-muted-foreground font-medium">Resident</span>
            <span className="col-span-2 font-medium">{task.residentDisplayName} (Room {task.roomNumber})</span>
          </div>
          <div className="grid grid-cols-3 gap-2">
            <span className="text-muted-foreground font-medium">Task Type</span>
            <span className="col-span-2">{task.taskType}</span>
          </div>
          <div className="grid grid-cols-3 gap-2">
            <span className="text-muted-foreground font-medium">Status</span>
            <span className="col-span-2 capitalize">{task.status.toLowerCase()}</span>
          </div>
          <div className="grid grid-cols-3 gap-2">
            <span className="text-muted-foreground font-medium">Assigned To</span>
            <span className="col-span-2">{task.assignedCnaDisplayName || "Unassigned"}</span>
          </div>
          <div className="grid grid-cols-3 gap-2">
            <span className="text-muted-foreground font-medium">Scheduled</span>
            <span className="col-span-2">{format(new Date(task.scheduledTime), "PPpp")}</span>
          </div>
          {task.completedAt && (
            <div className="grid grid-cols-3 gap-2">
              <span className="text-muted-foreground font-medium">Completed At</span>
              <span className="col-span-2">{format(new Date(task.completedAt), "PPpp")}</span>
            </div>
          )}
          <div className="grid grid-cols-3 gap-2">
            <span className="text-muted-foreground font-medium">Abnormal Flag</span>
            <span className="col-span-2">
              {task.isAbnormalFlagged ? (
                <span className="text-red-500 font-medium">Yes - Attention Required</span>
              ) : (
                "No"
              )}
            </span>
          </div>
          <div className="grid grid-cols-3 gap-2 border-t pt-2 mt-2">
            <span className="text-muted-foreground font-medium">Goal / Notes</span>
            <span className="col-span-2 text-muted-foreground">{task.goal || "None provided"}</span>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};
