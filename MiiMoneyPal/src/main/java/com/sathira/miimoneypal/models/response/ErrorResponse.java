package com.sathira.miimoneypal.models.response;

import java.time.Instant;
import java.util.List;

/**
 * Standard error response format for all API errors.
 * Provides consistent error structure across the application.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp,
        List<FieldError> fieldErrors
) implements ApiResponse {

    /**
     * Create error response without field errors.
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this(status, error, message, path, Instant.now(), null);
    }

    /**
     * Create error response with field errors (for validation failures).
     */
    public ErrorResponse(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        this(status, error, message, path, Instant.now(), fieldErrors);
    }

    /**
     * Represents a single field validation error.
     */
    public record FieldError(
            String field,
            String message,
            Object rejectedValue
    ) {
    }
}
