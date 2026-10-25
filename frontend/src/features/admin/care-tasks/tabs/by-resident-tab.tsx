import { useCareTasks } from "../hooks/useCareTasks";
import { ResidentCardHeader } from "../components/by-resident-tab/resident-card-header";
import { ResidentTaskTableHeader } from "../components/by-resident-tab/resident-task-table-header";
import { ResidentTaskRow } from "../components/by-resident-tab/resident-task-row";
import { ResidentTaskFilterBar } from "../components/by-resident-tab/resident-task-filter-bar";
import { Table, TableBody } from "@/components/ui/table";
import { ClipboardList } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";

import type { GroupedByResidentCard, EnrichedTaskRow } from "@/services/care-tasks-api";
import { format } from "date-fns";

export const ByResidentTab = () => {
  const [page, setPage] = useState(0);
  const [date, setDate] = useState<Date | undefined>(new Date("2026-07-14"));
  const [status, setStatus] = useState<string>("all");
  const [taskType, setTaskType] = useState<string>("all");
  const [flag, setFlag] = useState<string>("all");
  const [searchResident, setSearchResident] = useState<string>("");

  const params = {
    page,
    size: 10,
    date: date ? format(date, "yyyy-MM-dd") : undefined,
    ...(status !== "all" && { status }),
    ...(taskType !== "all" && { taskType }),
    ...(flag === "flagged" && { isAbnormalFlagged: true }),
    ...(flag === "unflagged" && { isAbnormalFlagged: false }),
  };

  const { residentGroupsData, isLoadingResidents } = useCareTasks(params);
  let residentGroups: GroupedByResidentCard[] = Array.isArray(residentGroupsData?.data) ? residentGroupsData.data : [];
  const meta = residentGroupsData?.metadata;

  // Extract available task types dynamically from current data
  const availableTaskTypes = Array.from(
    new Set(
      residentGroups.flatMap((group) => group.tasks.map((task) => task.taskType))
    )
  ).sort();

  if (searchResident.trim() !== "") {
    residentGroups = residentGroups.filter((g: GroupedByResidentCard) => 
      (g.residentDisplayName || '').toLowerCase().includes(searchResident.toLowerCase())
    );
  }

  if (isLoadingResidents) {
    return (
      <div className="flex items-center justify-center py-20 text-muted-foreground text-sm gap-2">
        <div className="size-4 border-2 border-muted-foreground/30 border-t-muted-foreground rounded-full animate-spin" />
        Loading care tasks…
      </div>
    );
  }

  return (
    <div className="flex flex-col w-full gap-5 mt-5">
      {/* Filter bar */}
      <ResidentTaskFilterBar
        date={date}
        setDate={setDate}
        status={status}
        setStatus={setStatus}
        taskType={taskType}
        setTaskType={setTaskType}
        flag={flag}
        setFlag={setFlag}
        searchResident={searchResident}
        setSearchResident={setSearchResident}
        availableTaskTypes={availableTaskTypes}
      />

      {residentGroups.length === 0 ? (
        <div className="mt-5 flex flex-col items-center justify-center gap-3 py-20 rounded-xl border bg-card text-center">
          <ClipboardList className="size-10 text-muted-foreground/40" />
          <p className="text-sm text-muted-foreground">
            No residents with care tasks found for the selected filters.
          </p>
        </div>
      ) : (
        <>
          {/* One card per Resident */}
          {residentGroups.map((resident: GroupedByResidentCard) => {
            // Map DTO to match what ResidentCardHeader expects
            const residentMapped = {
              id: String(resident.residentId),
              name: resident.residentDisplayName,
              age: 0,
              room: resident.roomNumber || "N/A",
              imageUrl: `https://ui-avatars.com/api/?name=${encodeURIComponent(resident.residentDisplayName)}&background=random`,
              careLevel: "Standard Care" as const,
              statusDot: "active" as const,
              totalTasks: resident.totalTasks,
              completedTasks: resident.completedTasks,
              missedTasks: resident.missedTasks,
            };

            return (
              <div
                key={resident.residentId}
                className="rounded-xl border border-border bg-card shadow-sm overflow-hidden"
              >
                {/* Card header — Resident identity + status */}
                <ResidentCardHeader resident={residentMapped} />

                {/* Table */}
                <div className="overflow-x-auto">
                  <Table className="table-fixed min-w-[720px]">
                    <ResidentTaskTableHeader />
                    <TableBody>
                      {resident.tasks.map((task: EnrichedTaskRow) => (
                        <ResidentTaskRow key={task.id} task={task} />
                      ))}
                    </TableBody>
                  </Table>
                </div>
              </div>
            );
          })}

          {/* Pagination Controls */}
          {meta && meta.totalPage > 1 && (
            <div className="flex items-center justify-between pt-4">
              <div className="text-sm text-muted-foreground">
                Showing page {meta.currentPage} of {meta.totalPage}
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  disabled={!meta.hasPrevious}
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                >
                  Previous
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  disabled={!meta.hasNext}
                  onClick={() => setPage((p) => p + 1)}
                >
                  Next
                </Button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};
