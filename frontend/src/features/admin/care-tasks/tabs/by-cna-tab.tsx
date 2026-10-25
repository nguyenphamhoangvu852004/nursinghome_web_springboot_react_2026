import { useCareTasks } from "../hooks/useCareTasks";
import { CnaCardHeader } from "../components/by-cna-tab/cna-card-header";
import { CareTaskTableHeader } from "../components/by-cna-tab/care-task-table-header";
import { CareTaskRow } from "../components/by-cna-tab/care-task-row";
import { CareTaskFilterBar } from "../components/by-cna-tab/care-task-filter-bar";
import { Table, TableBody } from "@/components/ui/table";
import { ClipboardList } from "lucide-react";
import { useState } from "react";
import { Button } from "@/components/ui/button";

import type { GroupedByCnaCard, EnrichedTaskRow } from "@/services/care-tasks-api";
import { format } from "date-fns";

export const ByCnaTab = () => {
  const [page, setPage] = useState(0);
  const [date, setDate] = useState<Date | undefined>(new Date("2026-07-14"));
  const [status, setStatus] = useState<string>("all");
  const [taskType, setTaskType] = useState<string>("all");
  const [flag, setFlag] = useState<string>("all");
  const [searchCna, setSearchCna] = useState<string>("");

  const params = {
    page,
    size: 10,
    date: date ? format(date, "yyyy-MM-dd") : undefined,
    ...(status !== "all" && { status }),
    ...(taskType !== "all" && { taskType }),
    ...(flag === "flagged" && { isAbnormalFlagged: true }),
    ...(flag === "unflagged" && { isAbnormalFlagged: false }),
  };

  const { cnaGroupsData, isLoading } = useCareTasks(params);
  let cnaGroups: GroupedByCnaCard[] = Array.isArray(cnaGroupsData?.data) ? cnaGroupsData.data : [];
  const meta = cnaGroupsData?.metadata;

  // Extract available task types dynamically from current data
  const availableTaskTypes = Array.from(
    new Set(
      cnaGroups.flatMap((group) => group.tasks.map((task) => task.taskType))
    )
  ).sort();

  if (searchCna.trim() !== "") {
    cnaGroups = cnaGroups.filter((g: GroupedByCnaCard) => 
      (g.cnaDisplayName || 'Unassigned').toLowerCase().includes(searchCna.toLowerCase())
    );
  }

  if (isLoading) {
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
      <CareTaskFilterBar
        date={date}
        setDate={setDate}
        status={status}
        setStatus={setStatus}
        taskType={taskType}
        setTaskType={setTaskType}
        flag={flag}
        setFlag={setFlag}
        searchCna={searchCna}
        setSearchCna={setSearchCna}
        availableTaskTypes={availableTaskTypes}
      />

      {cnaGroups.length === 0 ? (
        <div className="mt-5 flex flex-col items-center justify-center gap-3 py-20 rounded-xl border bg-card text-center">
          <ClipboardList className="size-10 text-muted-foreground/40" />
          <p className="text-sm text-muted-foreground">
            No care tasks found for the selected filters.
          </p>
        </div>
      ) : (
        <>
          {/* One card per CNA */}
          {cnaGroups.map((cnaGroup: GroupedByCnaCard) => (
            <div
              key={cnaGroup.cnaId || 'unassigned'}
              className="rounded-xl border border-border bg-card shadow-sm overflow-hidden"
            >
              {/* Card header — CNA identity + progress */}
              <CnaCardHeader
                name={cnaGroup.cnaDisplayName || 'Unassigned'}
                role={"CERTIFIED NURSING ASSISTANT"}
                imageUrl={`https://ui-avatars.com/api/?name=${encodeURIComponent(cnaGroup.cnaDisplayName || 'Unassigned')}&background=random`}
                imageAlt={"CNA profile"}
                totalTasks={cnaGroup.totalTasks}
                completedTasks={cnaGroup.completedTasks}
              />

              {/*
                Table with table-fixed so the explicit widths on <th> are
                honoured — this is what makes columns perfectly aligned.
              */}
              <div className="overflow-x-auto">
                <Table className="table-fixed min-w-[720px]">
                  <CareTaskTableHeader />
                  <TableBody>
                    {cnaGroup.tasks.map((task: EnrichedTaskRow) => (
                      <CareTaskRow key={task.id} task={task} />
                    ))}
                  </TableBody>
                </Table>
              </div>
            </div>
          ))}
          
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
