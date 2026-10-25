export interface StaffingRatioConfig {
    id: number;
    facilityId: number;
    minHrsPerResidentDay: number;
    warnBelowPercentage: number;
}

export interface UpdateStaffingRatioPayload {
    minHrsPerResidentDay: number;
    warnBelowPercentage: number;
}
