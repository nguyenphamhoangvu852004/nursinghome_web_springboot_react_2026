import type { CarePlan } from "@/services/care-plan/care-plan-types";

export function getCarePlanStatusStyle(carePlan: CarePlan) {
  switch (carePlan.status) {
    case "Draft":
      return {
        className: "bg-gray-100 text-gray-600 border-gray-300",
      };

    case "Pending review":
      return {
        className: "bg-yellow-100 text-yellow-700 border-yellow-300",
      };

    case "Active":
      return {
        className: "bg-green-100 text-green-700 border-green-300",
      };

    case "Review due":
      return {
        className: "bg-orange-100 text-orange-700 border-orange-300",
      };

    case "Need update":
      return {
        className: "bg-red-100 text-red-700 border-red-300",
      };

    case "Archived":
      return {
        className: "bg-slate-100 text-slate-600",
      };
    default:
      return {
        label: "Invalid state",
        className: "",
      };
  }
}

export function getCarePlanLOCTierStyle(carePlan: CarePlan) {
  switch (carePlan.LOCTier) {
    case 1:
      return {
        className: "bg-green-100 text-green-700 border-green-300",
      };

    case 2:
      return {
        className: "bg-blue-100 text-blue-700 border-blue-300",
      };

    case 3:
      return {
        className: "bg-orange-100 text-orange-700 border-orange-300",
      };

    case 4:
      return {
        className: "bg-red-100 text-red-700 border-red-300",
      };

    case 5:
      return {
        className: "bg-red-300 text-red-700 border-red-300",
      };

    default:
      return {
        label: "Invalid loc tier",
        className: "",
      };
  }
}
