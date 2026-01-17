package com.sathira.miimoneypal.records.user;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain record representing a user in the system.
 * Contains user identity, authentication, and preferences.
 *
 * IMPORTANT: passwordHash should NEVER be exposed in API responses.
 * Use Presenters to transform to response DTOs without sensitive fields.
 */
@Builder
public record User(
        Long id,
        String email,
        String passwordHash,
        String currencySymbol,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Default currency symbol if not specified.
     */
    public static final String DEFAULT_CURRENCY = "LKR";

    /**
     * Compact constructor for validation.
     * Required fields: email, passwordHash (for new users)
     */
    public User {
        Objects.requireNonNull(email, "email must not be null");
        // passwordHash can be null when loading user without password (e.g., for display)
        // currencySymbol defaults to LKR if null
        if (currencySymbol == null) {
            currencySymbol = DEFAULT_CURRENCY;
        }
    }

    /**
     * Check if this user has a persisted ID.
     */
    public boolean isPersisted() {
        return id != null;
    }

    /**
     * Check if the given email matches this user's email (case-insensitive).
     */
    public boolean hasEmail(String emailToCheck) {
        return email != null && email.equalsIgnoreCase(emailToCheck);
    }
}
