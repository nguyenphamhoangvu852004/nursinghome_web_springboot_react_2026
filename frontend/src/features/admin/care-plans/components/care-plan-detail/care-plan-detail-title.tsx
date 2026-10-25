import Title from "../../ui/Title";

type CarePlanDetailTitleProps = {
  residentName?: string;
};

export default function CarePlanDetailTitle({
  residentName,
}: CarePlanDetailTitleProps) {
  return (
    <Title>
      <p>{`Care Plans - ${residentName ?? "Nguyen Vu"}`}</p>
    </Title>
  );
}
