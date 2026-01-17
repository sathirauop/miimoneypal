package com.sathira.miimoneypal.constants;

/**
 * Centralized API endpoint paths.
 * All REST endpoints should reference these constants to avoid hardcoding URLs.
 */
public final class EndPoints {

    private EndPoints() {
        // Prevent instantiation
    }

    // Base path for all API endpoints
    public static final String API_BASE = "/api";

    // ===========================================
    // Auth Endpoints
    // ===========================================
    public static final String AUTH = API_BASE + "/auth";
    public static final String AUTH_LOGIN = AUTH + "/login";
    public static final String AUTH_REGISTER = AUTH + "/register";
    public static final String AUTH_REFRESH = AUTH + "/refresh";
    public static final String AUTH_LOGOUT = AUTH + "/logout";
    public static final String AUTH_ME = AUTH + "/me";

    // ===========================================
    // Transaction Endpoints
    // ===========================================
    public static final String TRANSACTIONS = API_BASE + "/transactions";
    public static final String TRANSACTIONS_BY_ID = TRANSACTIONS + "/{id}";

    // ===========================================
    // Category Endpoints
    // ===========================================
    public static final String CATEGORIES = API_BASE + "/categories";
    public static final String CATEGORIES_BY_ID = CATEGORIES + "/{id}";
    public static final String CATEGORIES_ARCHIVE = CATEGORIES_BY_ID + "/archive";
    public static final String CATEGORIES_MERGE = CATEGORIES_BY_ID + "/merge";

    // ===========================================
    // Bucket Endpoints
    // ===========================================
    public static final String BUCKETS = API_BASE + "/buckets";
    public static final String BUCKETS_BY_ID = BUCKETS + "/{id}";
    public static final String BUCKETS_ARCHIVE = BUCKETS_BY_ID + "/archive";
    public static final String BUCKETS_MARK_SPENT = BUCKETS_BY_ID + "/mark-spent";

    // ===========================================
    // Dashboard Endpoints
    // ===========================================
    public static final String DASHBOARD = API_BASE + "/dashboard";
    public static final String DASHBOARD_MONTHLY = DASHBOARD + "/monthly";

    // ===========================================
    // Settings Endpoints
    // ===========================================
    public static final String SETTINGS = API_BASE + "/settings";
    public static final String SETTINGS_PROFILE = SETTINGS + "/profile";
    public static final String SETTINGS_PREFERENCES = SETTINGS + "/preferences";
}
