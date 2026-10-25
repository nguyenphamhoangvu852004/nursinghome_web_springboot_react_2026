import { Info } from "lucide-react";
import { useEffect, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useIncidentSeverityLevels } from "../hooks/use-incident-severity";
import {
  createIncidentSeverityLevel,
  updateIncidentSeverityLevel,
} from "../services/incident-severity-service";
import { AdminPageShell, AdminPageShellNote, badgeClassOptions } from "@/components/common/admin-page-shell";
import { SeverityRow } from "@/components/common/severity-row";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AddSeverityTab } from "../tabs/add-severity-tab";

const defaultSeverityContent = {
  description: "Description unavailable.",
  example: "Example unavailable.",
};

type IncidentSeverityRow = {
  id: number;
  levelName: string;
  description: string;
  example: string;
  chartLockTrigger: boolean;
  isEditing: boolean;
};

const IncidentSeverityPage = () => {
  const queryClient = useQueryClient();
  const { data, isLoading, isError } = useIncidentSeverityLevels();
  const [rows, setRows] = useState<IncidentSeverityRow[]>([]);
  const [newSeverityName, setNewSeverityName] = useState("");
  const [newChartLockTrigger, setNewChartLockTrigger] = useState(false);
  const [newDescription, setNewDescription] = useState("");
  const [newExample, setNewExample] = useState("");
  const [newSeverityError, setNewSeverityError] = useState<string | null>(null);

  useEffect(() => {
    if (!data) return;

    setRows(
      data.map((item) => ({
        id: item.id,
        levelName: item.levelName,
        description: item.description?.trim() || defaultSeverityContent.description,
        example: item.example?.trim() || defaultSeverityContent.example,
        chartLockTrigger: item.chartLockTrigger,
        isEditing: false,
      }))
    );
  }, [data]);

  const handleEdit = (id: number) => {
    setRows((current) =>
      current.map((row) =>
        row.id === id ? { ...row, isEditing: true } : row
      )
    );
  };

  const handleCancel = (id: number) => {
    const originalRow = data?.find((item) => item.id === id);
    if (!originalRow) {
      setRows((current) =>
        current.map((row) =>
          row.id === id ? { ...row, isEditing: false } : row
        )
      );
      return;
    }

    setRows((current) =>
      current.map((row) =>
        row.id === id
          ? {
              ...row,
              levelName: originalRow.levelName,
              description: originalRow.description?.trim() || defaultSeverityContent.description,
              example: originalRow.example?.trim() || defaultSeverityContent.example,
              isEditing: false,
            }
          : row
      )
    );
  };

  const handleSave = async (id: number) => {
    const row = rows.find((r) => r.id === id);
    if (!row) return;

    try {
      await updateIncidentSeverityLevel(id, {
        level_name: row.levelName,
        chart_lock_trigger: row.chartLockTrigger,
        description: row.description,
        example: row.example,
      });
      setRows((current) =>
        current.map((item) =>
          item.id === id ? { ...item, isEditing: false } : item
        )
      );
      queryClient.invalidateQueries({ queryKey: ["incidentSeverityLevels"] });
    } catch (error) {
      console.error("Failed to save incident severity row", error);
    }
  };

  const handleCreateSeverity = async () => {
    if (!newSeverityName.trim()) {
      setNewSeverityError("Level name is required.");
      return;
    }

    try {
      await createIncidentSeverityLevel({
        level_name: newSeverityName.trim(),
        chart_lock_trigger: newChartLockTrigger,
        description: newDescription.trim(),
        example: newExample.trim(),
      });
      setNewSeverityName("");
      setNewChartLockTrigger(false);
      setNewDescription("");
      setNewExample("");
      setNewSeverityError(null);
      queryClient.invalidateQueries({ queryKey: ["incidentSeverityLevels"] });
      setActiveTab("list");
    } catch (error) {
      console.error("Failed to create incident severity", error);
      setNewSeverityError("Unable to create severity. Please try again.");
    }
  };

  const handleFieldChange = (
    id: number,
    field: keyof Omit<IncidentSeverityRow, "id" | "isEditing" | "chartLockTrigger">,
    value: string
  ) => {
    setRows((current) =>
      current.map((row) =>
        row.id === id ? { ...row, [field]: value } : row
      )
    );
  };

  const [activeTab, setActiveTab] = useState("list");

  return (
    <AdminPageShell
      breadcrumbs={[
        { label: "Admin" },
        { label: "Incident Severity", active: true },
      ]}
      title="Incident Severity Levels"
      subtitle="Taxonomy used across Incident & Risk (M7) — 4 levels, fixed"
      infoBanner={{
        icon: <Info className="w-5 h-5" />,
        text: "Fixed 4-level taxonomy, referenced across all M7 Incident wireframes. Only descriptions are editable.",
      }}
    >
      <div className="glass-card rounded-2xl overflow-hidden shadow-sm">
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <div className="flex flex-col gap-4 px-6 py-4 border-b border-outline-variant/20 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="text-body-sm text-on-surface-variant">Create and manage incident severity levels.</p>
            </div>
            <TabsList className="w-full md:w-auto">
              <TabsTrigger value="list">List</TabsTrigger>
              <TabsTrigger value="create">Create</TabsTrigger>
            </TabsList>
          </div>
          <TabsContent value="list">
            <div className="overflow-x-auto w-full">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low border-b border-outline-variant/30">
                <th className="px-6 py-4 font-label-bold text-label-bold text-outline uppercase tracking-wider w-32">
                  Level
                </th>
                <th className="px-6 py-4 font-label-bold text-label-bold text-outline uppercase tracking-wider">
                  Description
                </th>
                <th className="px-6 py-4 font-label-bold text-label-bold text-outline uppercase tracking-wider">
                  Example
                </th>
                <th className="px-6 py-4 font-label-bold text-label-bold text-outline uppercase tracking-wider text-right">
                  Action
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20">
              {isLoading ? (
                <tr>
                  <td colSpan={4} className="px-6 py-8 text-center text-body-base text-on-surface-variant">
                    Loading incident severity levels...
                  </td>
                </tr>
              ) : isError ? (
                <tr>
                  <td colSpan={4} className="px-6 py-8 text-center text-body-base text-error">
                    Error loading incident severity levels. Please try again later.
                  </td>
                </tr>
              ) : (
                rows.map((row, index) => {
                  const badgeClasses = badgeClassOptions[index % badgeClassOptions.length];

                  return (
                    <SeverityRow
                      key={row.id}
                      level={row.levelName}
                      badgeClasses={badgeClasses}
                      isEditing={row.isEditing}
                      onEdit={() => handleEdit(row.id)}
                      viewLevelCell={
                        <td className="px-6 py-6 align-top">
                          <span className={`px-3 py-1 font-label-bold text-sm rounded-full border ${badgeClasses}`}>
                            {row.levelName}
                          </span>
                        </td>
                      }
                      viewCells={
                        <>
                          <td className="px-6 py-6 align-top">
                            <p className="text-body-base font-body-base text-on-surface">
                              {row.description}
                            </p>
                          </td>
                          <td className="px-6 py-6 align-top">
                            <p className="text-body-sm font-body-sm text-on-surface-variant italic">
                              {row.example}
                            </p>
                          </td>
                        </>
                      }
                      editLevelCell={
                        <td className="px-6 py-6 align-top">
                          <Input
                            type="text"
                            value={row.levelName}
                            onChange={(event) => handleFieldChange(row.id, "levelName", event.target.value)}
                            className="text-body-base font-body-base"
                          />
                        </td>
                      }
                      editCells={
                        <>
                          <td className="px-6 py-6 align-top">
                            <Input
                              type="text"
                              value={row.description}
                              onChange={(event) => handleFieldChange(row.id, "description", event.target.value)}
                              className="text-body-base font-body-base"
                            />
                          </td>
                          <td className="px-6 py-6 align-top">
                            <Input
                              type="text"
                              value={row.example}
                              onChange={(event) => handleFieldChange(row.id, "example", event.target.value)}
                              className="text-body-sm font-body-sm"
                            />
                          </td>
                        </>
                      }
                      onSave={() => handleSave(row.id)}
                      onCancel={() => handleCancel(row.id)}
                    />
                  );
                })
              )}
            </tbody>
          </table>
            </div>
          </TabsContent>
          <TabsContent value="create">
            <AddSeverityTab
              newSeverityName={newSeverityName}
              newChartLockTrigger={newChartLockTrigger}
              newDescription={newDescription}
              newExample={newExample}
              newSeverityError={newSeverityError}
              onSeverityNameChange={setNewSeverityName}
              onChartLockTriggerChange={setNewChartLockTrigger}
              onDescriptionChange={setNewDescription}
              onExampleChange={setNewExample}
              onCreate={handleCreateSeverity}
            />
          </TabsContent>
        </Tabs>
        <AdminPageShellNote>
          Levels cannot be added or deleted here. Chart-lock (BR-07) triggers on every incident regardless of severity — severity only determines the external-reporting deadline (see SLA Configuration, AD-09).
        </AdminPageShellNote>
      </div>
    </AdminPageShell>
  );
};

export default IncidentSeverityPage;
