package com.sathira.miimoneypal.exception;

/**
 * Exception thrown when a business rule is violated.
 * Results in HTTP 422 Unprocessable Entity response.
 * Examples: insufficient bucket balance, negative usable amount prevention.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
