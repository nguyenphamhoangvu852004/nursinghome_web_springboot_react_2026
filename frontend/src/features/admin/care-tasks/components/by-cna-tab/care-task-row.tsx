import { AlertTriangle, CheckCircle2, MoreVertical, Clock } from "lucide-react";
import type { EnrichedTaskRow } from "@/services/care-tasks-api";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { TableCell, TableRow } from "@/components/ui/table";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { format } from "date-fns";
import { useState } from "react";
import { ConfirmationDialog } from "../modals/ConfirmationDialog";
import { AssignCnaModal } from "../modals/AssignCnaModal";
import { RescheduleTaskModal } from "../modals/RescheduleTaskModal";
import { TaskDetailModal } from "../modals/TaskDetailModal";
import { EditTaskModal } from "../modals/EditTaskModal";
import { useCareTasks } from "../../hooks/useCareTasks";
import { toast } from "sonner";

interface CareTaskRowProps {
  task: EnrichedTaskRow;
}

export const CareTaskRow = ({ task }: CareTaskRowProps) => {
  const { completeTask, markMissed, flagAbnormal, rescheduleTask, assignCna, deleteTask, updateTask } = useCareTasks();
  
  const [showConfirmComplete, setShowConfirmComplete] = useState(false);
  const [showConfirmMissed, setShowConfirmMissed] = useState(false);
  const [showConfirmDelete, setShowConfirmDelete] = useState(false);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showRescheduleModal, setShowRescheduleModal] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);

  const isCompleted = task.status === "COMPLETED";
  const isMissed = task.status === "MISSED";

  const timeFormatted = format(new Date(task.scheduledTime), "hh:mm a");

  // ── Status badge ─────────────────────────────────────────────────────────
  const statusBadge = () => {
    if (isCompleted) {
      return (
        <Badge className="gap-1 border-transparent bg-emerald-50 text-emerald-700 dark:bg-emerald-900/25 dark:text-emerald-400 font-medium text-[11px] px-2 py-0.5 h-auto whitespace-nowrap">
          <CheckCircle2 className="size-3 shrink-0" />
          Done
        </Badge>
      );
    }
    if (isMissed) {
      return (
        <Badge className="gap-1 border-transparent bg-red-50 text-red-600 dark:bg-red-900/25 dark:text-red-400 font-medium text-[11px] px-2 py-0.5 h-auto whitespace-nowrap">
          <AlertTriangle className="size-3 shrink-0" />
          Missed
        </Badge>
      );
    }
    return (
      <Badge className="gap-1 border-transparent bg-amber-50 text-amber-700 dark:bg-amber-900/25 dark:text-amber-400 font-medium text-[11px] px-2 py-0.5 h-auto whitespace-nowrap">
        <Clock className="size-3 shrink-0" />
        Pending
      </Badge>
    );
  };

  // ── Row accent colour applied via first-cell left border ─────────────────
  const accentBorder = isMissed
    ? "border-l-[3px] border-l-red-400"
    : isCompleted
    ? "border-l-[3px] border-l-emerald-400"
    : "border-l-[3px] border-l-amber-400";

  const rowBg = isMissed
    ? "bg-red-50/40 hover:bg-red-50/70 dark:bg-red-950/10 dark:hover:bg-red-950/20"
    : "hover:bg-muted/40";

  return (
    <>
      <TableRow
        className={`group transition-colors border-b border-border/60 last:border-b-0 ${rowBg}`}
      >
        {/* ── Resident ─────────────────────────────────────── w-[240px] */}
        <TableCell className={`px-4 py-3 w-[240px] min-w-[200px] ${accentBorder}`}>
          <div className="flex items-center gap-2.5">
            <Avatar className="size-8 flex-shrink-0">
              <AvatarImage
                src={`https://ui-avatars.com/api/?name=${encodeURIComponent(task.residentDisplayName)}&background=random`}
                alt={`Portrait of ${task.residentDisplayName}`}
              />
              <AvatarFallback className="bg-primary/10 text-primary text-[11px] font-semibold">
                {task.residentDisplayName
                  .split(" ")
                  .map((n) => n[0])
                  .join("")
                  .slice(0, 2)}
              </AvatarFallback>
            </Avatar>
            <div className="flex flex-col min-w-0">
              <div className="flex items-center gap-1.5">
                <span className="text-sm font-medium text-foreground truncate leading-tight">
                  {task.residentDisplayName}
                </span>
              </div>
              <span className="text-[11px] text-muted-foreground leading-tight mt-0.5">
                Room {task.roomNumber || "N/A"} <span className="mx-0.5 opacity-50">•</span> Care Plan #{task.carePlanId}
              </span>
            </div>
          </div>
        </TableCell>

        {/* ── Task Type ─────────────────────────────────────── w-[130px] */}
        <TableCell className="px-4 py-3 w-[130px]">
          <Badge
            className={`text-[11px] font-medium px-2 py-0.5 h-auto border-transparent whitespace-nowrap ${
              isMissed
                ? "bg-muted text-muted-foreground"
                : isCompleted
                ? "bg-blue-50 text-blue-700 dark:bg-blue-900/25 dark:text-blue-400"
                : "bg-violet-50 text-violet-700 dark:bg-violet-900/25 dark:text-violet-400"
            }`}
          >
            {task.taskType}
          </Badge>
        </TableCell>

        {/* ── Goal ──────────────────────────────────────────── auto width */}
        <TableCell className="px-4 py-3">
          <p className="text-xs text-muted-foreground line-clamp-2 leading-relaxed max-w-xs">
            {task.goal}
          </p>
        </TableCell>

        {/* ── Scheduled Time ───────────────────────────────── w-[110px] */}
        <TableCell className="px-4 py-3 w-[110px] text-center">
          <span
            className={`inline-flex items-center justify-center text-[11px] font-mono font-semibold px-2 py-1 rounded-md tabular-nums ${
              isMissed
                ? "bg-red-50 text-red-600 dark:bg-red-900/20 dark:text-red-400"
                : isCompleted
                ? "bg-muted text-muted-foreground"
                : "bg-muted text-foreground"
            }`}
          >
            {timeFormatted}
          </span>
        </TableCell>

        {/* ── Status ───────────────────────────────────────── w-[110px] */}
        <TableCell className="px-4 py-3 w-[110px] text-center">
          <div className="flex items-center justify-center">
            {statusBadge()}
          </div>
        </TableCell>

        {/* ── Flags ────────────────────────────────────────── w-[70px] */}
        <TableCell className="px-4 py-3 w-[70px] text-center">
          <div className="flex items-center justify-center">
            {task.isAbnormalFlagged ? (
              <span
                className="inline-flex size-6 items-center justify-center rounded-full bg-red-100 text-red-500 dark:bg-red-900/30 dark:text-red-400 ring-1 ring-red-200 dark:ring-red-800"
                title="Abnormal findings flagged"
              >
                <AlertTriangle className="size-3.5" />
              </span>
            ) : (
              <span
                className="inline-flex size-6 items-center justify-center rounded-full text-muted-foreground/30 group-hover:text-muted-foreground/60 transition-colors"
                title="No abnormal findings"
              >
                <CheckCircle2 className="size-3.5" />
              </span>
            )}
          </div>
        </TableCell>

        {/* ── Actions ──────────────────────────────────────── w-[160px] */}
        <TableCell className="px-4 py-3 w-[160px]">
          <div className="flex items-center justify-end gap-1.5">
            {isCompleted ? (
              <span className="inline-flex items-center gap-1 text-[11px] font-medium text-muted-foreground bg-muted px-2.5 py-1 rounded-md">
                <CheckCircle2 className="size-3 text-emerald-500 shrink-0" />
                Completed
              </span>
            ) : (
              <Button
                variant={isMissed ? "outline" : "default"}
                size="sm"
                onClick={() => isMissed ? setShowRescheduleModal(true) : setShowConfirmComplete(true)}
                className={`h-7 text-xs px-3 font-medium ${
                  isMissed
                    ? "border-border text-foreground hover:bg-muted"
                    : "bg-primary text-primary-foreground hover:bg-primary/90 shadow-sm"
                }`}
              >
                {isMissed ? "Reschedule" : "Complete"}
              </Button>
            )}

            <DropdownMenu>
              <DropdownMenuTrigger
                className="inline-flex size-7 flex-shrink-0 items-center justify-center rounded-md text-muted-foreground hover:bg-muted hover:text-foreground transition-colors outline-none focus-visible:ring-2 focus-visible:ring-ring/50"
                aria-label="More options"
              >
                <MoreVertical className="size-3.5" />
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="min-w-[150px]">
                <DropdownMenuItem onClick={() => setShowDetailModal(true)}>
                  View Details
                </DropdownMenuItem>
                <DropdownMenuItem onClick={() => setShowEditModal(true)}>
                  Edit Task
                </DropdownMenuItem>
                {!isCompleted && !isMissed && (
                  <DropdownMenuItem onClick={() => setShowConfirmComplete(true)}>
                    Complete Task
                  </DropdownMenuItem>
                )}
                {!isCompleted && !isMissed && (
                  <DropdownMenuItem onClick={() => setShowAssignModal(true)}>
                    Re-assign CNA
                  </DropdownMenuItem>
                )}
                {!isCompleted && (
                  <DropdownMenuItem onClick={() => setShowRescheduleModal(true)}>
                    Reschedule
                  </DropdownMenuItem>
                )}
                <DropdownMenuItem onClick={() => flagAbnormal({ taskId: task.id, isAbnormalFlagged: !task.isAbnormalFlagged })}>
                  {task.isAbnormalFlagged ? "Unflag Abnormal" : "Flag as Abnormal"}
                </DropdownMenuItem>
                {!isCompleted && !isMissed && (
                  <DropdownMenuItem variant="destructive" onClick={() => setShowConfirmMissed(true)}>
                    Mark as Missed
                  </DropdownMenuItem>
                )}
                <DropdownMenuItem variant="destructive" onClick={() => setShowConfirmDelete(true)}>
                  Delete Task
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </TableCell>
      </TableRow>

      <TaskDetailModal 
        open={showDetailModal}
        onOpenChange={setShowDetailModal}
        task={task}
      />

      <EditTaskModal 
        open={showEditModal}
        onOpenChange={setShowEditModal}
        task={task}
        onConfirm={async (payload) => {
          try {
            await updateTask({ taskId: task.id, payload });
            toast.success("Task updated successfully");
          } catch (err: any) {
            toast.error(err.message || "Failed to update task");
          }
        }}
      />

      <ConfirmationDialog
        open={showConfirmComplete}
        onOpenChange={setShowConfirmComplete}
        title="Complete Task"
        description="Are you sure you want to mark this task as completed?"
        onConfirm={async () => {
          try {
            await completeTask({ taskId: task.id });
            toast.success("Task completed successfully");
          } catch (err: any) {
            toast.error(err.message || "Failed to complete task");
          } finally {
            setShowConfirmComplete(false);
          }
        }}
      />

      <ConfirmationDialog
        open={showConfirmMissed}
        onOpenChange={setShowConfirmMissed}
        title="Mark Task Missed"
        description="Are you sure you want to mark this task as missed? This indicates the care event did not occur."
        isDestructive
        onConfirm={async () => {
          try {
            await markMissed(task.id);
            toast.success("Task marked as missed");
          } catch (err: any) {
            toast.error(err.message || "Failed to mark task missed");
          } finally {
            setShowConfirmMissed(false);
          }
        }}
      />

      <ConfirmationDialog
        open={showConfirmDelete}
        onOpenChange={setShowConfirmDelete}
        title="Delete Task"
        description="Are you sure you want to permanently delete this task? This action cannot be undone."
        isDestructive
        onConfirm={async () => {
          try {
            await deleteTask(task.id);
            toast.success("Task deleted successfully");
          } catch (err: any) {
            toast.error(err.message || "Failed to delete task");
          } finally {
            setShowConfirmDelete(false);
          }
        }}
      />

      {showAssignModal && (
        <AssignCnaModal
          open={showAssignModal}
          onOpenChange={setShowAssignModal}
          currentCnaId={task.assignedCnaId}
          onConfirm={async (cnaId) => {
            try {
              await assignCna({ taskId: task.id, assignedCnaId: cnaId });
              toast.success("Assignment updated successfully");
            } catch (err: any) {
              toast.error(err.message || "Failed to update assignment");
            }
          }}
        />
      )}

      {showRescheduleModal && (
        <RescheduleTaskModal
          open={showRescheduleModal}
          onOpenChange={setShowRescheduleModal}
          currentDate={task.scheduledTime}
          onConfirm={async (dateString) => {
            try {
              await rescheduleTask({ taskId: task.id, scheduledTime: dateString });
              toast.success("Task rescheduled successfully");
            } catch (err: any) {
              toast.error(err.message || "Failed to reschedule task");
            }
          }}
        />
      )}
    </>
  );
};
