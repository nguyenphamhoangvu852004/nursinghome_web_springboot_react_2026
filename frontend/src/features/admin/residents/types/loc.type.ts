export interface AdlItem {

    activity: string;

    score: number;

    description: string;

}

export interface LocResult {

    adlScore: number;

    suggestedLevelCode: string;

    suggestedLevelName: string;

    currentLevelCode: string;

    currentLevelName: string;

    dailyRate: number;

    adlItems: AdlItem[];

}

export interface CareLevelHistory {

    id: number;

    careLevelId: number;

    levelCode: string;

    startDate: string;
     action: string; 

    endDate?: string | null;

}


export interface AssessmentItem {
  metricId: number;
  category: string;
  metricName: string;
  score: number;
  notes?: string;
}

export interface LocClassificationResult {
  assessmentId: number;
  adlTotalScore: number;

  suggestedCareLevelId: number;
  suggestedCareLevelCode: string;
  suggestedCareLevelName: string;

  confirmedCareLevelId: number;
  confirmedCareLevelCode: string;
  confirmedCareLevelName: string;

  overridden: boolean;
  assessedBy: string;

  details: AssessmentItem[];
}
export interface LocClassificationResult {

    residentId: number;

    residentName: string;

    adlTotalScore: number;

    suggestedCareLevelCode: string;

    suggestedCareLevelName: string;

    createdAt: string;

    metrics: LocMetric[];

    estimatedDailyRate: number;

    confirmed: boolean;
}


export interface LocMetric {

    metricName: string;

    category: string;

    score: number;

    notes?: string;

}