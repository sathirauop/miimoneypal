package com.sathira.miimoneypal.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * Results in HTTP 409 Conflict response.
 * Examples: duplicate email, duplicate category name for same type.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String field, String value) {
        super(String.format("%s already exists with %s: %s", resourceName, field, value));
    }
}
