import { AlarmClock, Clock, Database, Paperclip } from "lucide-react";
import Card from "../ui/card";
import type {
  CarePlan,
  CarePlanMetadata,
} from "@/services/care-plan/care-plan-types";

type CarePlanStatisticalProps = {
  carePlans: CarePlan[];
  carePlanMetadata: CarePlanMetadata;
};

export default function CarePlanStatistical({
  carePlans,
  carePlanMetadata,
}: CarePlanStatisticalProps) {
  const statistics = carePlans.reduce(
    (acc, carePlan) => {
      switch (carePlan.status) {
        case "Draft":
          acc.draft++;
          break;

        case "Pending review":
          acc.pendingReview++;
          break;

        case "Review due":
          acc.reviewDue++;
          break;
      }

      return acc;
    },
    {
      draft: 0,
      pendingReview: 0,
      reviewDue: 0,
    },
  );

  const cards = [
    {
      title: "Total plans",
      amount: carePlanMetadata.totalElements,
      icon: Database,
      className: "bg-blue-100 text-blue-600",
    },
    {
      title: "Draft",
      amount: statistics.draft,
      icon: Paperclip,
      className: "bg-gray-100 text-gray-600",
    },
    {
      title: "Pending Review",
      amount: statistics.pendingReview,
      icon: Clock,
      className: "bg-yellow-100 text-yellow-600",
    },
    {
      title: "Review Due",
      amount: statistics.reviewDue,
      icon: AlarmClock,
      className: "bg-orange-100 text-orange-600",
    },
  ];

  return (
    <div className="flex flex-wrap gap-3 mt-8">
      {cards.map((card) => (
        <div className="basis-[calc((100%-36px)/4)] rounded-xl border">
          <Card
            key={card.title}
            icon={card.icon}
            title={card.title}
            amount={card.amount.toString()}
            className={` ${card.className} bg-amber-200`}
            width="basis-[calc((100%-36px)/4)]"
            height="h-[120px]"
          />
        </div>
      ))}
    </div>
  );
}
