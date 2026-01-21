package com.sathira.miimoneypal.rest.auth.login;

import com.sathira.miimoneypal.models.response.ApiResponse;

/**
 * Response DTO for successful login.
 * Returns user info and JWT tokens.
 */
public record LoginResponse(
    Long userId,
    String email,
    String currencySymbol,
    String accessToken,
    String refreshToken,
    Long accessTokenExpiresIn,
    Long refreshTokenExpiresIn
) implements ApiResponse {}
