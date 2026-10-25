import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { SystemActionTab } from "../tabs/system-action-tab";
import { PhiAccessTab } from "../tabs/phi-access-tab";
import { ActiveSessionsTab } from "../tabs/active-sessions-tab";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";

const tabs = [
  {
    value: "system-actions",
    label: "System Actions",
    permission: PERMISSIONS.AUDIT_LOG_VIEW,
    content: <SystemActionTab />,
  },
  {
    value: "phi-access",
    label: "PHI Access",
    permission: PERMISSIONS.PHI_ACCESS_LOG_VIEW,
    content: <PhiAccessTab />,
  },
  {
    value: "active-sessions",
    label: "Active Sessions",
    permission: PERMISSIONS.AUDIT_LOG_VIEW, // reuse audit log view perm
    content: <ActiveSessionsTab />,
  },
];

const AuditlogPage = () => {
  const { can } = usePermissions();
  const visibleTabs = tabs.filter((t) => can(t.permission));

  return (
    <Tabs defaultValue={visibleTabs[0]?.value}>
      <TabsList className="w-full">
        {visibleTabs.map((t) => (
          <TabsTrigger key={t.value} value={t.value}>
            {t.label}
          </TabsTrigger>
        ))}
      </TabsList>
      {visibleTabs.map((t) => (
        <TabsContent key={t.value} value={t.value}>
          {t.content}
        </TabsContent>
      ))}
    </Tabs>
  );
};

export default AuditlogPage;
