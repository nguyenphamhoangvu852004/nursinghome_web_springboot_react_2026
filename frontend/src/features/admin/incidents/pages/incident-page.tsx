import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ListTab } from "../tabs/list-tab";
import { ReportTab } from "../tabs/report-tab";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";

const tabs = [
  {
    value: "list",
    label: "List",
    permission: PERMISSIONS.INCIDENTS_LIST_VIEW,
    content: <ListTab />,
  },
  {
    value: "report",
    label: "Report",
    permission: PERMISSIONS.INCIDENTS_VIEW,
    content: <ReportTab />,
  },
];

const IncidentPage = () => {
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

export default IncidentPage;
