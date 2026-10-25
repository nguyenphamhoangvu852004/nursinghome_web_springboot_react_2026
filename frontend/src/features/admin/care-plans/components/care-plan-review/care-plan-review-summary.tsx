import type {
  CarePlanGoal,
  CarePlanIntervention,
} from "@/services/care-plan/care-plan-types";
import Title from "../../ui/Title";
import Text from "../../ui/Text";

type CarePlanReviewPlanSummaryProps = {
  goals: CarePlanGoal[];
};

function CarePlanReviewPlanSummary({ goals }: CarePlanReviewPlanSummaryProps) {
  const interventions: CarePlanIntervention[] = goals.flatMap(
    (goal) => goal.interventions ?? [],
  );

  return (
    <div className="rounded-lg border-2 border-gray-200 bg-[#fafcfe] p-4">
      <Title className="mb-5">Plan Summary (read-only)</Title>

      {/* Goals */}
      <section className="mb-6">
        <Title className="mb-3 text-[22px]">Goals</Title>

        {goals.length > 0 ? (
          <ul className="list-disc pl-5 space-y-2">
            {goals.map((goal) => (
              <li key={goal.id}>
                <Text>{goal.goalDescription}</Text>
              </li>
            ))}
          </ul>
        ) : (
          <Text>No goals available.</Text>
        )}
      </section>

      {/* Interventions */}
      <section>
        <Title className="mb-3 text-[22px]">Interventions</Title>

        {interventions.length > 0 ? (
          <ul className="list-disc pl-5 space-y-2">
            {interventions.map((intervention) => (
              <li key={intervention.id}>
                <Text>
                  {intervention.title}
                  <span className="ml-2 text-gray-500">
                    ({intervention.assignedRole})
                  </span>
                </Text>
              </li>
            ))}
          </ul>
        ) : (
          <Text>No interventions available.</Text>
        )}
      </section>
    </div>
  );
}

export default CarePlanReviewPlanSummary;
