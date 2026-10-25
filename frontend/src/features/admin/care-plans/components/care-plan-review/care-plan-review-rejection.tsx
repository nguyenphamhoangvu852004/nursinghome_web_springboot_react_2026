import { cn } from "@/lib/utils";
import Text from "../../ui/Text";
import Title from "../../ui/Title";
import { Input } from "@/components/ui/input";

type CarePlanReviewRejectionProps = {
  className?: string;
};

function CarePlanReviewRejection({ className }: CarePlanReviewRejectionProps) {
  return (
    <div
      className={cn(
        `bg-[#fafcfe] border-2 border-solid border-gray-200 rounded-[8px] p-[8px]`,
        className,
      )}
    >
      <div className="flex flex-start gap-3">
        <Title className="text-[16px] mb-[6px]">Rejection reason</Title>

        <Text className="">(Required if rejecting)</Text>
      </div>

      <div>
        <Input placeholder="Add a reason to return this plan to the nurse as Draft..."></Input>
      </div>
    </div>
  );
}

export default CarePlanReviewRejection;
