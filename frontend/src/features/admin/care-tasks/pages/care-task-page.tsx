import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ByCnaTab } from "../tabs/by-cna-tab";
import { ByResidentTab } from "../tabs/by-resident-tab";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";

const tabs = [
  {
    value: "by-cna",
    label: "By CNA",
    permission: PERMISSIONS.CARE_TASK_VIEW,
    content: <ByCnaTab />,
  },
  {
    value: "by-resident",
    label: "By Resident",
    permission: PERMISSIONS.CARE_TASK_VIEW,
    content: <ByResidentTab />,
  },
];

const CareTaskPage = () => {
  const { can } = usePermissions();
  const visibleTabs = tabs.filter((t) => can(t.permission));

  return (
    <div className="flex flex-col w-full gap-5">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-3 border-b border-border pb-5">
        <div className="flex flex-col gap-0.5">
          <h1 className="text-[22px] font-semibold tracking-tight text-foreground leading-tight">
            Care Tasks
          </h1>
          <p className="text-sm text-muted-foreground">
            Manage daily assignments and task execution.
          </p>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue={visibleTabs[0]?.value} className="gap-0">
        <TabsList className="w-full">
          {visibleTabs.map((t) => (
            <TabsTrigger key={t.value} value={t.value} className="px-5">
              {t.label}
            </TabsTrigger>
          ))}
        </TabsList>
        {visibleTabs.map((t) => (
          <TabsContent key={t.value} value={t.value} className="mt-0">
            {t.content}
          </TabsContent>
        ))}
      </Tabs>
    </div>
  );
};

export default CareTaskPage;
