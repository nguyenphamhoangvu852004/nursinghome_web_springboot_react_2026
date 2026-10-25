import { cn } from "@/lib/utils";
import Text from "../../ui/Text";
import Title from "../../ui/Title";
import Flag from "../../ui/flag";

type CarePlanReviewIDTProps = {
  className?: string;
};
function CarePlanReviewIDT({ className }: CarePlanReviewIDTProps) {
  return (
    <div
      className={cn(
        `bg-[#fafcfe] border-2 border-solid border-gray-200 rounded-[8px] p-[8px]`,
        className,
      )}
    >
      <Title className="text-[16px] mb-[6px]">IDT Acknowlegdment</Title>

      <Text>Auto-derived -- no: Don-tickable(54A2)</Text>
      <div className="flex flex-row justify-between">
        <div>
          <Text>Pysician</Text>
          <Text>Name</Text>
        </div>
        <div className="flex flex-col">
          <Flag title="Signed"></Flag>
          <Text>YYYY-MM-DD HH-MM</Text>
        </div>
      </div>

      <div className="flex flex-row justify-between">
        <div>
          <Text>Pysician</Text>
          <Text>Name</Text>
        </div>
        <div className="flex flex-col">
          <Flag title="Signed"></Flag>
          <Text>YYYY-MM-DD HH-MM</Text>
        </div>
      </div>
    </div>
  );
}

export default CarePlanReviewIDT;
