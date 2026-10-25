import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import EquipmentTab from "../tabs/equipment-tab";
import CategoryTab from "../tabs/category-tab";
import SupplyTab from "../tabs/supply-tab";

export default function EquipmentPage() {
  return (
     <Tabs defaultValue="equipment">
      <TabsList className={"w-full"}>
        <TabsTrigger value="equipment">Equipment</TabsTrigger>
         <TabsTrigger value="supplies">Supplies</TabsTrigger>
        <TabsTrigger value="categories">Categories</TabsTrigger>
      </TabsList>
      <TabsContent value="equipment">
        <EquipmentTab />
      </TabsContent>
      <TabsContent value="categories">
        <CategoryTab />
      </TabsContent>
      <TabsContent value="supplies">
        <SupplyTab />
      </TabsContent>
    </Tabs>
  );
}