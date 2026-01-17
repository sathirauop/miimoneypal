package com.sathira.miimoneypal.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Fine-grained permissions for MiiMoneyPal features.
 * Used with @PreAuthorize("hasAuthority('PERMISSION_NAME')") on controllers/use cases.
 */
@Getter
@RequiredArgsConstructor
public enum Permission {
    // Transaction permissions
    TRANSACTION_READ("transaction:read"),
    TRANSACTION_WRITE("transaction:write"),

    // Category permissions
    CATEGORY_READ("category:read"),
    CATEGORY_WRITE("category:write"),

    // Bucket permissions
    BUCKET_READ("bucket:read"),
    BUCKET_WRITE("bucket:write"),

    // Dashboard permissions
    DASHBOARD_READ("dashboard:read"),

    // Settings permissions
    SETTINGS_READ("settings:read"),
    SETTINGS_WRITE("settings:write");

    private final String permission;
}
