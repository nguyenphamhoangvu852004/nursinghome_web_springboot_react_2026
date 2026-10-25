import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { RoomsBedTab } from "../tabs/rooms-bed-tab";
import { CareLevelRateTab } from "../tabs/care-level-rate-tab";
import { PERMISSIONS } from "@/common/permissions";
import { usePermissions } from "@/features/auth/hooks/use-current-user";
import { InventoryTab } from "../tabs/inventory-tab";

const tabs = [
  {
    value: "rooms-beds",
    label: "Rooms & Beds",
    permission: PERMISSIONS.FACILITY_VIEW,
    content: <RoomsBedTab />,
  },
  {
    value: "care-level-rates",
    label: "Care Level Rates",
    permission: PERMISSIONS.FACILITY_VIEW,
    content: <CareLevelRateTab />,
  },
  {
    value: "inventory",
    label: "Inventory",
    permission: PERMISSIONS.INVENTORY_VIEW,
    content: <InventoryTab />,
  },
];

export const FacilityDetailPage = () => {
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
