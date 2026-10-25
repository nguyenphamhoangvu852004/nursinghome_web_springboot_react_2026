import CarePlanReviewComplianceChecklist from "../components/care-plan-review/care-plan-review-compliance-checklist";
import CarePlanReviewAuthor from "../components/care-plan-review/care-plan-review-author";
import CarePlanReviewRejection from "../components/care-plan-review/care-plan-review-rejection";
import CarePlanReviewPlanSummary from "../components/care-plan-review/care-plan-review-summary";
import CarePlanReviewTitle from "../components/care-plan-review/care-plan-review-title";
import CarePlanReviewDecision from "../components/care-plan-review/care-plan-review-decision";
import CarePlanReviewIDT from "../components/care-plan-review/care-plan-review-idt";
import Flag from "../ui/flag";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { useParams } from "react-router";
import type { CarePlanDetail } from "@/services/care-plan/care-plan-types";
import { useEffect, useState } from "react";
import {
  approveCarePlan,
  getCarePlanDetail,
} from "@/services/care-plan/care-plan-services";
import { toast } from "sonner";

export default function CarePlanReviewPage() {
  const { id } = useParams();

  const [carePlan, setCarePlan] = useState<CarePlanDetail | null>(null);

  useEffect(() => {
    if (!id) return;

    loadCarePlan(Number(id));
  }, [id]);

  const loadCarePlan = async (id: number) => {
    try {
      const response = await getCarePlanDetail(id);
      setCarePlan(response.data);
    } catch (error) {
      console.error(error);
    }
  };
  if (!carePlan) {
    return <div>is loading...</div>;
  }
  const handleApprove = async () => {
    if (!id) return;

    try {
      await approveCarePlan(Number(id));

      toast.success("Care plan approved successfully.");

      await loadCarePlan(Number(id));
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message);
      }
    }
  };

  //get detail
  return (
    <div className="p-8  bg-gray-200">
      <Breadcrumb>
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/admin/care-plans">
              Care Planning
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink href="/admin/care-plans/review">
              Review
            </BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbLink href="/admin/care-plans/review">
              {carePlan.resident.fullname}
            </BreadcrumbLink>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>
      <div className="flex flex-row items-center gap-3">
        <CarePlanReviewTitle
          residentName={carePlan.resident.fullname}
        ></CarePlanReviewTitle>
        <Flag title={carePlan.status}></Flag>
      </div>
      <div className="grid grid-cols-[60%_40%] gap-3">
        <div className="flex flex-col">
          <CarePlanReviewPlanSummary
            goals={carePlan.goals}
          ></CarePlanReviewPlanSummary>
          <CarePlanReviewAuthor
            author={carePlan.createdBy}
            updatedAt={carePlan.updatedAt}
            className="mt-[16px]"
          ></CarePlanReviewAuthor>
          <CarePlanReviewRejection className="mt-[16px]"></CarePlanReviewRejection>
        </div>
        <div className="">
          <div className="flex flex-col">
            <CarePlanReviewComplianceChecklist></CarePlanReviewComplianceChecklist>
            <CarePlanReviewIDT className="mt-[16px]"></CarePlanReviewIDT>
            <CarePlanReviewDecision
              actorReviewName="Hardcode Name"
              actorReviewRoleName="Hardcode Rolename"
              onApprove={handleApprove}
              className="mt-[16px]"
            ></CarePlanReviewDecision>
          </div>
        </div>
      </div>
    </div>
  );
}
