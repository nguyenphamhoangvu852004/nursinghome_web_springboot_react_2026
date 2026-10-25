import { CARE_PLAN_API_BASE_URL } from "@/common/config/api-url";
import { apiClient } from "@/lib/api-client";
import type { ApiResponse } from "@/types/api";

export interface GoalInput {
  problemOrNeed: string; title: string; desiredOutcome: string; baselineValue: string;
  targetValue?: string; targetUnit?: string; measurementMethod?: string;
  targetDate?: string; priority: string; residentPreference?: string;
}
export interface InterventionInput {
  title: string; instructions: string; assignedRole: string; frequencyCode: string;
  responsibleDiscipline?: string; scheduleDefinition?: string; startDate?: string; endDate?: string;
  prnCriteria?: string; evidenceOrRationale?: string; requiresTaskGeneration: boolean; sortOrder: number;
}
export interface Goal extends GoalInput { id: number; carePlanVersionId: number; status: string }
export interface Intervention extends InterventionInput { id: number; careGoalId: number }
export interface PlanContent {
  id: number; state: string;
  versions: { id: number; versionNumber: number; status: string;
    goals: { goal: Goal; interventions: Intervention[] }[] }[];
}
export async function getPlanContent(id: number, signal?: AbortSignal) {
  const { data } = await apiClient.get<ApiResponse<PlanContent>>(`/care-plans/${id}/content`, { signal, baseURL: CARE_PLAN_API_BASE_URL });
  if (data.statusCode >= 400) throw new Error(data.message);
  return data.data;
}
export async function createGoal(versionId: number, input: GoalInput) {
  const { data } = await apiClient.post<ApiResponse<Goal>>(`/care-plan-versions/${versionId}/goals`, input, { baseURL: CARE_PLAN_API_BASE_URL });
  if (data.statusCode >= 400) throw new Error(data.message);
  return data.data;
}
export async function createIntervention(goalId: number, input: InterventionInput) {
  const { data } = await apiClient.post<ApiResponse<Intervention>>(`/care-goals/${goalId}/interventions`, input, { baseURL: CARE_PLAN_API_BASE_URL });
  if (data.statusCode >= 400) throw new Error(data.message);
  return data.data;
}
