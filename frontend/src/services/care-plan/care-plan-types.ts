export type ResidentInfo = {
  id: number;
  fullname: string;
  dateOfBirth: string;
};
export type ResidentDefinition = {
  room: string;
  bed: string;
};
export type CarePlanAuthor = {
  id: number;
  fullname: string;
  role: string;
};

export type CostEstimation = {
  locTierRatePerDay: string;
  locTierRatePerMonth: string;
  locTierRate: string;
  bedRate: string;
};
export type CarePlan = {
  id: number;
  LOCTier: number;
  cycle: number;
  status:
    | "Draft"
    | "Active"
    | "Need update"
    | "Review due"
    | "Archived"
    | "Pending review";

  significantFlag: boolean;
  goalCount: number;
  interventionCount: number;

  createdAt: string;
  updatedAt: string;

  isDeleted: boolean;

  createdBy: CarePlanAuthor;
  lastReviewedBy: string | null;
  lastReviewedDateTime: string | null;
  nextReviewDateTime: string | null;

  resident: ResidentInfo;

  definition: ResidentDefinition;
};

export type CarePlanMetadata = {
  currentPage: number;
  totalPage: number;
  currentLimit: number;
  hasNext: boolean;
  hasPrevious: boolean;
  totalElements: number;
};

export type GetCarePlanListResponse = {
  metadata: CarePlanMetadata;
  data: {
    list: CarePlan[];
  };
  message: string;
  statusCode: number;
};

export type CarePlanDetail = {
  id: number;

  status: "DRAFT" | "ACTIVE" | "NEEDS_UPDATE" | "REVIEW_DUE" | "ARCHIVED";
  locTier: number;

  significantFlag: boolean;
  isDeleted: boolean;

  costEstimation: CostEstimation;
  createdAt: string;
  updatedAt: string;

  cycle: number;

  lastReviewedBy: string | null;
  lastReviewedDateTime: string | null;
  nextReviewDateTime: string | null;
  createdBy: CarePlanAuthor;
  resident: {
    id: number;
    fullname: string;
    dateOfBirth: string;
  };

  definition: {
    room: string;
    bed: string;
  };

  goals: CarePlanGoal[];
};

export type CarePlanGoal = {
  id: number;
  goalDescription: string;
  title: string;
  status: "NOT_STARTED" | "IN_PROGRESS" | "ACHIEVED" | "NOT_MET";
  interventions: CarePlanIntervention[] | null;
};

export type CarePlanIntervention = {
  id: number;
  title: string;
  assignedRole: string;
};

export type GetCarePlanDetailResponse = {
  statusCode: number;
  message: string;
  data: CarePlanDetail;
};
