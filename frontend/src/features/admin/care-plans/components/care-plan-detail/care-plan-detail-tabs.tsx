import { Tabs, TabsContent, TabsList, TabsTrigger } from "../../ui/tabs";
import CarePlainDetailTabActivity from "../../pages/care-plan-detail/tab/care-plan-detail-tab-acitivity";
import CarePlanDetailTabOverview from "../../pages/care-plan-detail/tab/care-plan-detail-tab-overview";
import CarePlanDetailTabCost from "../../pages/care-plan-detail/tab/care-plan-detail-tab-cost";
import type {
  CarePlanDetail,
  CarePlanGoal,
} from "@/services/care-plan/care-plan-types";

type CarePlanNavBarProps = {
  carePlanDetail: CarePlanDetail;
};
export default function CarePlanNavBar(props: CarePlanNavBarProps) {
  return (
    <div>
      <Tabs defaultValue="carePlanDetail" className="w-full">
        <TabsList>
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="activity">Activity</TabsTrigger>
          <TabsTrigger value="cost">Cost</TabsTrigger>
        </TabsList>

        <TabsContent value="overview">
          <CarePlanDetailTabOverview
            listCareGoals={props.carePlanDetail.goals}
            reviewCycle={props.carePlanDetail.cycle}
            lastReview={props.carePlanDetail.lastReviewedDateTime ?? null}
            nextReview={props.carePlanDetail.nextReviewDateTime ?? null}
            costEstimate={props.carePlanDetail.costEstimation}
            locTier={props.carePlanDetail.locTier.toString()}
          ></CarePlanDetailTabOverview>
        </TabsContent>

        <TabsContent value="activity">
          <CarePlainDetailTabActivity></CarePlainDetailTabActivity>
        </TabsContent>

        <TabsContent value="cost">
          <CarePlanDetailTabCost></CarePlanDetailTabCost>
        </TabsContent>
      </Tabs>
    </div>
  );
}
