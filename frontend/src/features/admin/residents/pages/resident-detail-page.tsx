import { useParams, useNavigate } from "react-router";
import { useState, useEffect } from "react";
import { ArrowLeft, Edit } from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { InfoTab } from "../tabs/info-tab";
import { CareLevelHistoryTab } from "../tabs/care-level-history";
import { SensitiveInfoTab } from "../tabs/sensitive-info";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";
import { residentService, type ResidentInfo } from "@/services/resident/residentService";
import ResidentContactsTab from "../tabs/resident-contacts-tab";
import LocResultTab from "../tabs/loc-result-tab";

const ResidentDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { can } = usePermissions();
  const residentId = Number(id);
  const [resident, setResident] = useState<ResidentInfo | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInfo = async () => {
      if (!id) return;
      try {
        setLoading(true);
        const data = await residentService.getResidentInfo(id);
        setResident(data);
      } catch (err) {
        console.error("Failed to fetch resident info:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchInfo();
  }, [id]);

  const tabs = [
    {
      value: "info",
      label: "Info",
      permission: PERMISSIONS.RESIDENT_VIEW,
      content: <InfoTab residentId={id || ""} />,
    },
    {
      value: "contacts",
      label: "Contacts",
      permission: PERMISSIONS.RESIDENT_VIEW,
      content: (
        <ResidentContactsTab
          residentId={residentId}
        />
      ),
    },
    {
      value: "care-level-history",
      label: "Care Level History",
      permission: PERMISSIONS.RESIDENT_VIEW,
      content: <CareLevelHistoryTab residentId={id || ""} />,
    },
    {
      value: "loc-result",
      label: "LOC Classification Result",
      permission: PERMISSIONS.RESIDENT_VIEW,
      content: <LocResultTab residentId={residentId} />,
    },
    {
      value: "sensitive-info",
      label: "Sensitive Info",
      permission: PERMISSIONS.RESIDENT_SENSITIVE_VIEW,
      content: <SensitiveInfoTab residentId={id || ""} />,
    },
  ];

  const visibleTabs = tabs.filter((t) => can(t.permission));

  if (loading) {
    return (
      <div className="flex items-center justify-center p-12 text-slate-800 dark:text-slate-100">
        <span className="font-semibold text-lg text-slate-400 animate-pulse">Loading resident details...</span>
      </div>
    );
  }

  const residentName = resident
    ? `${resident.firstName} ${resident.middleName ? resident.middleName + ' ' : ''}${resident.lastName}`
    : "Resident Detail";

  return (
    <div className="space-y-6 max-w-6xl mx-auto">
      {/* Breadcrumb & Navigation */}
      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-1.5 text-xs text-slate-400 dark:text-slate-500 font-medium">
          <button
            onClick={() => navigate("/admin/residents")}
            className="hover:text-primary transition-all flex items-center gap-1"
            id="btn-back-to-list"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Residents</span>
          </button>
          <span>&gt;</span>
          <span className="text-slate-600 dark:text-slate-350">{residentName}</span>
        </div>
        <div className="flex items-center justify-between mt-2">
          <h1 className="text-2xl font-bold text-slate-900 dark:text-white tracking-tight">
            {residentName}
          </h1>
          <div className="flex items-center gap-3">
            {resident && (
              <span className="px-3 py-1 bg-primary/10 text-primary border border-primary/20 text-xs font-semibold rounded-full">
                {resident.status}
              </span>
            )}
            <button
              onClick={() => navigate(`/admin/residents/${id}/edit`)}
              className="flex items-center gap-1.5 bg-primary hover:bg-primary/95 text-white px-4 py-2 rounded-lg text-sm font-semibold shadow-xs transition-all cursor-pointer"
              id="btn-edit-profile"
            >
              <Edit className="w-4 h-4" />
              <span>Edit Profile</span>
            </button>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue={visibleTabs[0]?.value} className="space-y-6">
        <TabsList className="bg-muted/50 p-1 rounded-xl border border-border/40">
          {visibleTabs.map((t) => (
            <TabsTrigger
              key={t.value}
              value={t.value}
              className="px-4 py-2 rounded-lg text-sm font-semibold transition-all data-active:bg-background data-active:text-foreground data-active:shadow-sm"
              id={`tab-trigger-${t.value}`}
            >
              {t.label}
            </TabsTrigger>
          ))}
        </TabsList>
        {visibleTabs.map((t) => (
          <TabsContent
            key={t.value}
            value={t.value}
            className="bg-card text-card-foreground rounded-xl border border-border/50 p-6 shadow-xs focus-visible:outline-hidden"
          >
            {t.content}
          </TabsContent>
        ))}
      </Tabs>
    </div>
  );
};

export default ResidentDetailPage;