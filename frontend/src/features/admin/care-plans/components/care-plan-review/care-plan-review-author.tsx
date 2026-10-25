import { cn } from "@/lib/utils";
import Text from "../../ui/Text";
import Title from "../../ui/Title";
import type { CarePlanAuthor } from "@/services/care-plan/care-plan-types";
import { formatOffsetDateTimeToDateTime } from "../../utils/time-utils";

type CarePlanReviewAuthorProps = {
  className?: string;
  author: CarePlanAuthor;
  updatedAt: string;
};
function CarePlanReviewAuthor({
  className,
  author,
  updatedAt,
}: CarePlanReviewAuthorProps) {
  return (
    <div
      className={cn(
        `bg-[#fafcfe] border-2 border-solid border-gray-200 rounded-[8px] p-[8px]`,
        className,
      )}
    >
      <Title className="text-[16px] mb-[6px]">Author Accountability</Title>
      <div id="interventions">
        <Text>
          Prepared by: {author.fullname} - {author.role}
        </Text>
        <Text>Prepared on: {formatOffsetDateTimeToDateTime(updatedAt)}</Text>
      </div>
    </div>
  );
}

export default CarePlanReviewAuthor;
