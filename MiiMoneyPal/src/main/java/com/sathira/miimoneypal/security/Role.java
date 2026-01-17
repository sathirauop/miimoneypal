package com.sathira.miimoneypal.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * User roles for MiiMoneyPal.
 * Currently supports a single USER role as this is a personal finance app.
 * Can be extended for admin functionality if needed.
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Set.of(
            Permission.TRANSACTION_READ,
            Permission.TRANSACTION_WRITE,
            Permission.CATEGORY_READ,
            Permission.CATEGORY_WRITE,
            Permission.BUCKET_READ,
            Permission.BUCKET_WRITE,
            Permission.DASHBOARD_READ,
            Permission.SETTINGS_READ,
            Permission.SETTINGS_WRITE
    ));

    private final Set<Permission> permissions;
}
