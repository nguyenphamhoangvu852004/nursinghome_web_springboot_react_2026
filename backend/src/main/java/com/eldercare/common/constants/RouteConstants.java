package com.eldercare.common.constants;

public final class RouteConstants {

    private RouteConstants() {
        // Prevent instantiation
    }

    // Base API Routes
    public static final String API_PREFIX = "/api/v1";
    public static final String ADMIN_API_PREFIX = "/api/v1/admin";
    
    // Facility Management Routes
    public static final String API_ADMIN_FACILITIES = ADMIN_API_PREFIX + "/facility-settings";

    // Inventory Management Routes
    public static final String API_ADMIN_INVENTORY = ADMIN_API_PREFIX + "/inventory";

    public static final String API_ADMIN_INVENTORY_CATEGORIES = API_ADMIN_INVENTORY + "/categories";

    public static final String API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT = API_ADMIN_INVENTORY + "/equipment";

    public static final String API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES = API_ADMIN_INVENTORY + "/supplies";

    // Task Management Routes
    public static final String API_TASKS = API_PREFIX + "/tasks";
    public static final String API_TASKS_SEARCH = API_TASKS + "/search";
    public static final String API_TASKS_BY_CNA = API_TASKS + "/by-cna";
    public static final String API_TASKS_BY_RESIDENT = API_TASKS + "/by-resident";
    
    // Single Task Operations
    public static final String API_TASK_BY_ID = API_TASKS + "/{taskId}";
    public static final String API_TASK_ASSIGN_CNA = API_TASK_BY_ID + "/assign-cna";
    public static final String API_TASK_COMPLETED = API_TASK_BY_ID + "/completed";
    public static final String API_TASK_MISSED = API_TASK_BY_ID + "/missed";
    public static final String API_TASK_FLAG_ABNORMAL = API_TASK_BY_ID + "/flag-abnormal";
    public static final String API_TASK_RESCHEDULE = API_TASK_BY_ID + "/reschedule";
    
    // Intervention Routes
    public static final String API_INTERVENTIONS = API_PREFIX + "/interventions";
    public static final String API_INTERVENTION_TASKS = API_INTERVENTIONS + "/{interventionId}/tasks";
    
    // User Routes
    public static final String API_USERS = API_PREFIX + "/users";
    public static final String API_USERS_CNAS = API_USERS + "/cnas";

}
