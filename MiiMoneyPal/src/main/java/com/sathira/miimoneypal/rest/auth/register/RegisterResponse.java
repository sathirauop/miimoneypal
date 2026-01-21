package com.sathira.miimoneypal.rest.auth.register;

import com.sathira.miimoneypal.models.response.ApiResponse;

/**
 * Response DTO for successful registration.
 * Returns user info and JWT tokens for immediate login.
 */
public record RegisterResponse(
    Long userId,
    String email,
    String currencySymbol,
    String accessToken,
    String refreshToken,
    Long accessTokenExpiresIn,   // milliseconds
    Long refreshTokenExpiresIn   // milliseconds
) implements ApiResponse {}
