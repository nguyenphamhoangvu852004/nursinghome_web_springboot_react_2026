import { Info } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { AdminPageShell, AdminPageShellNote, badgeClassOptions } from "@/components/common/admin-page-shell";
import { SeverityRow } from "@/components/common/severity-row";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AddSLATab } from "../tabs/add-sla-tab";
import { useSLAConfigs } from "../hooks/use-sla-config";
import { useIncidentSeverityLevels } from "@/features/admin/incident-severity/hooks/use-incident-severity";
import { createSLAConfig, updateSLAConfig } from "../services/sla-config-service";

const defaultSLAContent = {
    externalReportRequired: false,
    regulatoryBody: "Regulatory body unavailable.",
};

type SLAConfigRow = {
  id: number;
  severityId: number;
  severityName: string;
  slaWindowHrs: number;
  externalReportRequired: boolean;
  regulatoryBody: string;
  isEditing: boolean;
};

const SlaConfigPage = () => {
  const queryClient = useQueryClient();
  const { data: slaData, isLoading: slaLoading, isError: slaError } = useSLAConfigs();
  const { data: severityData, isLoading: severityLoading, isError: severityError } = useIncidentSeverityLevels();
  const [rows, setRows] = useState<SLAConfigRow[]>([]);
  const [newSLASeverityId, setNewSLASeverityId] = useState<number | null>(null);
  const [newSLAWindowHrs, setNewSLAWindowHrs] = useState("");
  const [newExternalReportRequired,setNewExternalReportRequired]=useState(false);
  const [newRegulatoryBody,setNewRegulatoryBody]=useState("");
  const [newSLAError, setNewSLAError] = useState<string | null>(null);

  console.log("SLA page hook data", { slaData, slaLoading, slaError, severityData, severityLoading, severityError });

  const isLoading = slaLoading || severityLoading;
  const isError = slaError || severityError;
  const severityMap = useMemo(
    () => new Map<number, string>(severityData?.map((item) => [item.id, item.levelName]) ?? []),
    [severityData]
  );

  useEffect(() => {
    if (!slaData) return;

    setRows(
      slaData.map((item) => ({
        id: item.id,
        severityId: item.severityId,
        severityName: severityMap.get(item.severityId) ?? "Unknown",
        slaWindowHrs: item.slaWindowHrs,
        externalReportRequired: item.externalReportRequired ?? defaultSLAContent.externalReportRequired,
        regulatoryBody: item.regulatoryBody ?? defaultSLAContent.regulatoryBody,
        isEditing: false,
      }))
    );
  }, [slaData, severityMap]);

  useEffect(() => {
    if (newSLASeverityId === null && severityData?.length) {
      setNewSLASeverityId(severityData[0].id);
    }
  }, [newSLASeverityId, severityData]);

  const handleEdit = (id: number) => {
    setRows((current) =>
      current.map((row) => (row.id === id ? { ...row, isEditing: true } : row))
    );
  };

  const handleCancel = (id: number) => {
    const originalRow = slaData?.find((item) => item.id === id);
    if (!originalRow) {
      setRows((current) =>
        current.map((row) => (row.id === id ? { ...row, isEditing: false } : row))
      );
      return;
    }

    setRows((current) =>
      current.map((row) =>
        row.id === id
          ? {
              ...row,
              severityName: severityMap.get(originalRow.severityId) ?? "Unknown",
              slaWindowHrs: originalRow.slaWindowHrs,
              externalReportRequired: originalRow.externalReportRequired ?? defaultSLAContent.externalReportRequired,
              regulatoryBody: originalRow.regulatoryBody ?? defaultSLAContent.regulatoryBody,
              isEditing: false,
            }
          : row
      )
    );
  };

  const handleSave = async (id: number) => {
    const row = rows.find((item) => item.id === id);
    if (!row) return;

    try {
      await updateSLAConfig(id, {
        severity_id: row.severityId,
        sla_window_hrs: Number(row.slaWindowHrs),
        external_report_required: row.externalReportRequired,
        regulatory_body: row.regulatoryBody,
      });
      setRows((current) =>
        current.map((item) => (item.id === id ? { ...item, isEditing: false } : item))
      );
      queryClient.invalidateQueries({ queryKey: ["slaConfigs"] });
    } catch (error) {
      console.error("Failed to save SLA row", error);
    }
  };

  const handleCreateSLA = async () => {
    if (!newSLASeverityId) {
      setNewSLAError("Severity is required.");
      return;
    }

    const hours = Number(newSLAWindowHrs);
    if (!newSLAWindowHrs || Number.isNaN(hours) || hours <= 0) {
      setNewSLAError("Please enter a valid SLA window in hours.");
      return;
    }

    try {
      await createSLAConfig({
        severity_id: newSLASeverityId,
        sla_window_hrs: hours,
        external_report_required: newExternalReportRequired,
        regulatory_body: newRegulatoryBody,
      });
      setNewSLAWindowHrs("");
      setNewExternalReportRequired(false);
      setNewRegulatoryBody("");
      setNewSLAError(null);
      queryClient.invalidateQueries({ queryKey: ["slaConfigs"] });
      setActiveTab("list");
    } catch (error) {
      console.error("Failed to create SLA config", error);
      setNewSLAError("Unable to create SLA config. Please try again.");
    }
  };

  const updateField = (
    id: number,
    field: "externalReportRequired" | "regulatoryBody",
    value: string
  ) => {
    setRows((current) =>
      current.map((row) => (row.id === id ? { ...row, [field]: value } : row))
    );
  };

  const updateNumberField = (id: number, value: string) => {
    const numericValue = Number(value);
    if (Number.isNaN(numericValue)) return;

    setRows((current) =>
      current.map((row) =>
        row.id === id ? { ...row, slaWindowHrs: numericValue } : row
      )
    );
  };

  const updateBooleanField = (
    id: number,
    value: boolean
  ) => {
      setRows((rows) =>
          rows.map((row) =>
              row.id === id
                  ? {
                        ...row,
                        externalReportRequired: value,
                    }
                  : row
          )
      );
  };

  const [activeTab, setActiveTab] = useState("list");

  console.log("SLA page severityMap", [...severityMap.entries()]);

  return (
    <AdminPageShell
      breadcrumbs={[
        { label: "Admin" },
        { label: "SLA Config", active: true },
      ]}
      title="SLA Configuration"
      subtitle="Regulatory reporting deadlines by incident severity (NFR-06)"
      infoBanner={{
        icon: <Info className="w-5 h-5" />,
        text: "Deadlines fixed to AD-08 severity tiers. Simulated — nothing is transmitted externally (NFR-05).",
      }}
    >
      <div className="glass-card rounded-2xl overflow-hidden shadow-sm">
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <div className="flex flex-col gap-4 px-6 py-4 border-b border-outline-variant/20 md:flex-row md:items-center md:justify-between">
            <div>
              <p className="text-body-sm text-on-surface-variant">Add or update SLA deadlines for incident severity tiers.</p>
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
                <th className="px-6 py-4 font-label-bold text-sm text-outline uppercase tracking-wider">
                  Severity
                </th>
                <th className="px-6 py-4 font-label-bold text-sm text-outline uppercase tracking-wider">
                  External Report Required
                </th>
                <th className="px-6 py-4 font-label-bold text-sm text-outline uppercase tracking-wider">
                  Reporting Deadline
                </th>
                <th className="px-6 py-4 font-label-bold text-sm text-outline uppercase tracking-wider">
                  Regulatory Body
                </th>
                <th className="px-6 py-4 font-label-bold text-sm text-outline uppercase tracking-wider text-right"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant/20">
              {isLoading ? (
                <tr>
                  <td colSpan={5} className="px-6 py-8 text-center text-body-base text-on-surface-variant">
                    Loading SLA configuration...
                  </td>
                </tr>
              ) : isError ? (
                <tr>
                  <td colSpan={5} className="px-6 py-8 text-center text-body-base text-error">
                    Error loading SLA configuration. Please try again later.
                  </td>
                </tr>
              ) : (
                rows.map((row, index) => {
                  const badgeClasses = badgeClassOptions[index % badgeClassOptions.length];

                  return (
                    <SeverityRow
                      key={row.id}
                      level={row.severityName}
                      badgeClasses={badgeClasses}
                      viewCells={
                        <>
                          <td className="px-6 py-6">
                            <p className="text-body-base font-body-base text-on-surface">
                              {row.externalReportRequired ? "Yes" : "No"}
                            </p>
                          </td>
                          <td className="px-6 py-6">
                            <p className="text-body-base font-body-base text-on-surface font-bold">
                              {row.slaWindowHrs} hours
                            </p>
                          </td>
                          <td className="px-6 py-6">
                            <p className="text-body-base font-body-base text-outline">
                              {row.regulatoryBody}
                            </p>
                          </td>
                        </>
                      }
                      isEditing={row.isEditing}
                      onEdit={() => handleEdit(row.id)}
                      editCells={
                        <>
                          <td className="px-6 py-6">
                            <input
                                type="checkbox"
                                checked={row.externalReportRequired}
                                onChange={(e) =>
                                    updateBooleanField(row.id, e.target.checked)
                                }
                            />
                          </td>
                          <td className="px-6 py-6">
                            <Input
                              type="text"
                              value={`${row.slaWindowHrs}`}
                              onChange={(event) => updateNumberField(row.id, event.target.value)}
                              className="text-body-base font-body-base"
                            />
                          </td>
                          <td className="px-6 py-6">
                            <Input
                              type="text"
                              value={row.regulatoryBody}
                              onChange={(event) => updateField(row.id, "regulatoryBody", event.target.value)}
                              className="text-body-base font-body-base"
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
            <AddSLATab
              severityOptions={severityData}
              selectedSeverityId={newSLASeverityId}
              slaWindowHrs={newSLAWindowHrs}
              externalReportRequired={newExternalReportRequired}
              regulatoryBody={newRegulatoryBody}
              errorMessage={newSLAError}
              onSeverityChange={setNewSLASeverityId}
              onSLAWindowChange={setNewSLAWindowHrs}
              onExternalReportRequiredChange={setNewExternalReportRequired}
              onRegulatoryBodyChange={setNewRegulatoryBody}
              onCreate={handleCreateSLA}
            />
          </TabsContent>
        </Tabs>
        <AdminPageShellNote>
          Only Deadline and Regulatory Body are editable. Values match M7 Incident List (SLA Countdown), Incident Detail, and Submit External Report modal (S6) — e.g. Major=24h, Moderate=48h window.
        </AdminPageShellNote>
      </div>
    </AdminPageShell>
  );
};

export default SlaConfigPage;
