package com.sathira.miimoneypal.rest.auth.refresh;

import com.sathira.miimoneypal.models.response.ApiResponse;

/**
 * Response DTO for successful token refresh.
 * Returns new access token (refresh token unchanged).
 */
public record RefreshResponse(
    String accessToken,
    Long accessTokenExpiresIn
) implements ApiResponse {}
