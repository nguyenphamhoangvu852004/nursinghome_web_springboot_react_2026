import {
  Timeline,
  TimelineContent,
  TimelineDate,
  TimelineHeader,
  TimelineIndicator,
  TimelineItem,
  TimelineSeparator,
  TimelineTitle,
} from "@/components/ui/timeline";
import Flag from "../../../ui/flag";
import type {
  CarePlanGoal,
  CostEstimation,
} from "@/services/care-plan/care-plan-types";
import { formatOffsetDateTimeToDate } from "../../../utils/time-utils";

export type CarePlanDetailTabOverviewProps = {
  listCareGoals: CarePlanGoal[];
  reviewCycle: number;
  lastReview: string | null;
  nextReview: string | null;
  costEstimate: CostEstimation;
  locTier: string;
};
export default function CarePlanDetailTabOverview({
  listCareGoals,
  reviewCycle,
  lastReview,
  nextReview,
  costEstimate,
  locTier,
}: CarePlanDetailTabOverviewProps) {
  return (
    <div className="w-full">
      <div>
        <p>Care Areas</p>
      </div>

      <div className="flex flex-col md:grid md:grid-cols-[70%_30%] gap-4">
        <div className="flex flex-col">
          <div className="careList">
            {listCareGoals.map((goal) => (
              <div
                key={goal.id}
                className="rounded-[6px] border-gray-200 bg-white border-2 p-[8px] mb-[16px]"
              >
                <div className="flex justify-between">
                  <p className="text-lg font-bold">{goal.title}</p>

                  <Flag title={goal.status} />
                </div>

                <div className="mt-2 text-gray-600">
                  <p>
                    <span className="font-medium">Goal:</span>{" "}
                    {goal.goalDescription}
                  </p>
                </div>

                {goal.interventions && goal.interventions.length > 0 && (
                  <div className="mt-4">
                    <p className="font-medium mb-2">Interventions</p>

                    <ul className="space-y-2">
                      {goal.interventions.map((intervention) => (
                        <li
                          key={intervention.id}
                          className="rounded bg-gray-50 p-2"
                        >
                          <p>{intervention.title}</p>

                          <p className="text-sm text-gray-500">
                            Assigned Role: {intervention.assignedRole}
                          </p>
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>
            ))}
          </div>
          <div className="activityTimeLineList">
            <p>Activity (Care Activity Timeline)</p>

            <div className="mt-[16px]">
              <Timeline className="w-full">
                <TimelineItem step={1}>
                  <TimelineIndicator className="size-2 bg-blue-600 border-blue-600" />
                  <TimelineSeparator className="bg-gray-300" />

                  <TimelineHeader>
                    <TimelineTitle>
                      Vitals recorded (SpO2 flagged)
                    </TimelineTitle>

                    <TimelineContent>
                      Marcus Rivera, CNA · 2026-07-02 14:05
                    </TimelineContent>
                  </TimelineHeader>
                </TimelineItem>

                <TimelineItem step={2}>
                  <TimelineIndicator className="size-2 bg-white border-2 border-blue-600" />
                  <TimelineSeparator className="bg-gray-300" />

                  <TimelineHeader>
                    <TimelineTitle>
                      Task "Ambulation assist" marked Done
                    </TimelineTitle>

                    <TimelineContent>
                      Marcus Rivera, CNA · 2026-07-02 08:30
                    </TimelineContent>
                  </TimelineHeader>
                </TimelineItem>

                <TimelineItem step={3}>
                  <TimelineIndicator className="size-2 bg-white border-2 border-blue-600" />
                  <TimelineSeparator className="bg-gray-300" />

                  <TimelineHeader>
                    <TimelineTitle>
                      Care plan approved & activated
                    </TimelineTitle>

                    <TimelineContent>
                      Denise Carter, DON · 2026-04-08 09:12
                    </TimelineContent>
                  </TimelineHeader>
                </TimelineItem>

                <TimelineItem step={4}>
                  <TimelineIndicator className="size-2 bg-white border-2 border-blue-600" />
                  <TimelineSeparator className="bg-gray-300" />

                  <TimelineHeader>
                    <TimelineTitle>Submitted for review</TimelineTitle>

                    <TimelineContent>
                      Anna Lee, RN · 2026-04-07 16:40
                    </TimelineContent>
                  </TimelineHeader>
                </TimelineItem>

                <TimelineItem step={5}>
                  <TimelineIndicator className="size-2 bg-white border-2 border-blue-600" />

                  <TimelineHeader>
                    <TimelineTitle>Care plan created</TimelineTitle>

                    <TimelineContent>
                      Anna Lee, RN · 2026-04-07 15:02
                    </TimelineContent>
                  </TimelineHeader>
                </TimelineItem>
              </Timeline>
            </div>
          </div>
        </div>
        <div>
          <div className="flex flex-col ">
            <div className="rounded-[6px] border-gray-200 bg-white border-2 p-[8px] mb-[16px]">
              <p className="text-lg font-bold">Cost Estimate</p>
              <div className=" flex flex-row justify-between">
                <p>Loc rate (Tier {locTier})</p>
                <p className="font-bold">{`$${costEstimate.locTierRate}`}</p>
              </div>
              <div>
                <p>Room rate (Semi-private)</p>
                <p className="font-bold">{`$${costEstimate.bedRate}`}</p>
              </div>
              <div className="border-b-2 border-gray-300"></div>
              <div>
                <div className=" flex flex-row justify-between">
                  <p className="text-md font-bold">Estimated daily</p>
                  <p className="font-bold">{`$${costEstimate.locTierRatePerDay}`}</p>
                </div>

                <div className=" flex flex-row justify-between">
                  <p className="text-md font-bold">Estimated monthly</p>

                  <p className="font-bold">{`$${costEstimate.locTierRatePerMonth}`}</p>
                </div>

                <div>
                  <p>Simualted - Not a billing transaction</p>
                </div>
              </div>
            </div>
            <div className="rounded-[6px] border-gray-200 bg-white border-2 p-[8px] mb-[16px]">
              <p className="text-lg font-bold">Review Cycle</p>
              <div className=" flex flex-row justify-between">
                <p>Last reviewed</p>
                <p className="font-bold">
                  {formatOffsetDateTimeToDate(lastReview)}
                </p>
              </div>
              <div className=" flex flex-row justify-between">
                <p>Next review due</p>
                <p>{formatOffsetDateTimeToDate(nextReview)}</p>
              </div>
              <div className=" flex flex-row justify-between">
                <p className="font-bold">Cycle</p>

                <p className="font-bold">{`${reviewCycle} days`}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
