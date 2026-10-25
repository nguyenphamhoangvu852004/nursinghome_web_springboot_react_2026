import Title from "../../ui/Title";

type CarePlanReviewTitle = {
  residentName?: string;
};

export default function CarePlanReivewTitle({
  residentName,
}: CarePlanReviewTitle) {
  return (
    <Title>
      <p className="font-bold">{`Review Care Plan - ${residentName}`}</p>
    </Title>
  );
}
