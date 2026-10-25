import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { RoleTab } from "../tabs/role-tab";
import { PermissionTab } from "../tabs/permission-tab";
import { UserTab } from "../tabs/user-tab";
import { PERMISSIONS } from "@/common/permissions";
import { CareLevelTab } from "../tabs/care-level-tab";
import { AssessmentMetricTab } from "../tabs/assessment_metric-tab";
import { usePermissions } from "@/features/auth/hooks/use-current-user";

const tabs = [
  {
    value: "roles",
    label: "Roles",
    permission: PERMISSIONS.ROLE_VIEW,
    content: <RoleTab />,
  },
  {
    value: "permissions",
    label: "Permissions",
    permission: PERMISSIONS.PERMISSION_VIEW,
    content: <PermissionTab />,
  },
  {
    value: "users",
    label: "Users",
    permission: PERMISSIONS.USER_VIEW,
    content: <UserTab />,
  },
  {
    value: "care-levels",
    label: "Care Levels",
    permission: PERMISSIONS.CARE_LEVEL_VIEW,
    content: <CareLevelTab />,
  },
  {
    value: "assessment-metrics",
    label: "Assessment Metrics",
    permission: PERMISSIONS.ASSESSMENT_METRIC_VIEW,
    content: <AssessmentMetricTab />,
  },
];

const SettingPage = () => {
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

export default SettingPage;
