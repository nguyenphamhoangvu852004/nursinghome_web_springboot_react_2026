// import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
// import { GoalTab } from "../tabs/goal-tab";
// import { InterventionTab } from "../tabs/intervention-tab";

import { useParams, useResolvedPath } from "react-router";
import CarePlanNavBar from "../../components/care-plan-detail/care-plan-detail-tabs";
import CarePlanDetailTitle from "../../components/care-plan-detail/care-plan-detail-title";
import CarePlanTitle from "../../components/care-plan-title";
import Flag from "../../ui/flag";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import type { CarePlanDetail } from "@/services/care-plan/care-plan-types";
import { getCarePlanDetail } from "@/services/care-plan/care-plan-services";
import { useEffect, useState } from "react";

// /*Deprecated*/

// const CarePlanDetailPage = () => {
//   return (
//     <Tabs defaultValue="goals">
//       <TabsList className={"w-full"}>
//         <TabsTrigger value="goals">Goals</TabsTrigger>
//         <TabsTrigger value="interventions">Interventions</TabsTrigger>
//       </TabsList>
//       <TabsContent value="goals">
//         <GoalTab />
//       </TabsContent>
//       <TabsContent value="interventions">
//         <InterventionTab />
//       </TabsContent>
//     </Tabs>
//   );
// };

// export default CarePlanDetailPage;

// type CarePlanDetailProps = {
//   id?: string;
//   residentName?: string;
//   status?: string;
//   room?: string;
//   locTier?: string;
//   nextReview?: string;
// };
export default function CarePlanDetailPage() {
  const { id } = useParams();

  const [carePlan, setCarePlan] = useState<CarePlanDetail | null>(null);

  useEffect(() => {
    if (!id) return;

    loadCarePlan(Number(id));
  }, [id]);

  const loadCarePlan = async (id: number) => {
    try {
      const response = await getCarePlanDetail(id);
      setCarePlan(response.data);
    } catch (error) {
      console.error(error);
    }
  };
  if (!carePlan) {
    return <div>is loading...</div>;
  }

  return (
    <div>
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/admin/care-plans">
              Care Planning
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink href={`/admin/care-plans/${carePlan.resident.id}`}>
              Detail
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink href={`/admin/care-plans/${carePlan.resident.id}`}>
              {carePlan.resident.fullname}
            </BreadcrumbLink>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
      <div className="flex items-center">
        <div className="mr-[16px] flex flex-row items-center gap-4">
          <div className="w-20 h-20 rounded-lg border-2 border-gray-400 overflow-hidden">
            <img
              src="https://placehold.co/80x80"
              alt=""
              className="w-full h-full object-cover"
            />
          </div>
          <div>
            <CarePlanDetailTitle
              residentName={carePlan.resident.fullname}
            ></CarePlanDetailTitle>
            <div>{`DOB ${carePlan.resident.dateOfBirth} . Room ${carePlan.definition.room}${carePlan.definition.bed} . Resident ID ${carePlan.resident.id}`}</div>
            <div className="flex flex-row gap-4 mt-[6px]">
              <Flag
                title={carePlan.status}
                className={`rounded-full ${
                  status === "Needs Update"
                    ? "bg-red-300 text-red-700 border-red-400"
                    : "bg-green-300 text-green-700 border-green-400"
                }`}
              ></Flag>
              <Flag
                title={`Level ${carePlan.locTier.toString()}`}
                className={`rounded-full ${
                  status === "Needs Update"
                    ? "bg-red-300 text-red-700 border-red-400"
                    : "bg-green-300 text-green-700 border-green-400"
                }`}
              ></Flag>
            </div>
          </div>
        </div>
      </div>
      {/* <div className=" text-gray-600">
        {`Room ${room ?? "204B"} . LOC Tier ${locTier ?? "3"} . Next review ${nextReview ?? "2026-07-07"}`}
      </div> */}

      <div className="tab mt-4">
        <CarePlanNavBar carePlanDetail={carePlan}></CarePlanNavBar>
      </div>
    </div>
  );
}
