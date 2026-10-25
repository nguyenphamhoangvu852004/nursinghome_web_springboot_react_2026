import { Button } from "@/components/ui/button";
import Title from "../../ui/Title";
import Text from "../../ui/Text";
import { cn } from "@/lib/utils";
import ApproveCarePlanDialog from "./approve-care-plan-dialog";

type CarePlanReviewDecisionProps = {
  className?: string;
  actorReviewName: string;
  actorReviewRoleName: string;

  onApprove: () => Promise<void>;
};
export default function CarePlanReviewDecision({
  className,
  actorReviewName,
  actorReviewRoleName,
  onApprove,
}: CarePlanReviewDecisionProps) {
  return (
    <div
      className={cn(
        `bg-[#fafcfe] border-2 border-solid border-gray-200 rounded-[8px] p-[8px]`,
        className,
      )}
    >
      <Title className="text-[16px] mb-[6px">Decision</Title>
      <div className="flex flex-col">
        <ApproveCarePlanDialog
          actorReviewName={actorReviewName}
          actorReviewRoleName={actorReviewRoleName}
          onApprove={onApprove}
        ></ApproveCarePlanDialog>

        <Button
          className={`min-h-14 bg-white  border-2 border-red-300 border-solid text-red-600 w-full `}
        >
          <Text className="font-bold text-red-600">Reject & Return</Text>
        </Button>
      </div>
    </div>
  );
}
