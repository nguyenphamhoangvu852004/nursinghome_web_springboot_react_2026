package com.eldercare.modules.careplan_management.careplan_design.service.loc_service;

public interface ILocRateEstimation {
    public String calculateCostDaily(double locTierRate, double bedRate);

    public String calculateCostMonthly(double locTierRate, double bedRate);
}
