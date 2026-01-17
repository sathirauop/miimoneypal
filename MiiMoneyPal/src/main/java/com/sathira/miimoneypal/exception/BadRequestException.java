package com.sathira.miimoneypal.exception;

/**
 * Exception thrown when the request is invalid or malformed.
 * Results in HTTP 400 Bad Request response.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
