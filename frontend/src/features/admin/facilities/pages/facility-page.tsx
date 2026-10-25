import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { FacilityTab } from "../tabs/facility-tab";
import { RoomsBedTab } from "../tabs/rooms-bed-tab";
import { CareLevelRateTab } from "../tabs/care-level-rate-tab";

const FacilityPage = () => {
  return (
    <Tabs defaultValue="facilities">
      <TabsList className={"w-full"}>
        <TabsTrigger value="facilities">Facilities</TabsTrigger>
        <TabsTrigger value="rooms-beds">Rooms & Beds</TabsTrigger>
        <TabsTrigger value="care-level-rates">Care Level Rates</TabsTrigger>
      </TabsList>
      <TabsContent value="facilities">
        <FacilityTab />
      </TabsContent>
      <TabsContent value="rooms-beds">
        <RoomsBedTab />
      </TabsContent>
      <TabsContent value="care-level-rates">
        <CareLevelRateTab />
      </TabsContent>
    </Tabs>
  );
};

export default FacilityPage;