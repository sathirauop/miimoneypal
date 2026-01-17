package com.sathira.miimoneypal.architecture;

import com.sathira.miimoneypal.models.response.ApiResponse;
import com.sathira.miimoneypal.security.AppUser;

/**
 * Base interface for use cases that require authenticated user context.
 * The AppUser is provided separately from the request to ensure user isolation.
 *
 * @param <REQUEST>  The input request type (usually a record with @Valid annotations)
 * @param <RESPONSE> The output response type (must implement ApiResponse)
 */
public interface AuthenticatedUseCase<REQUEST, RESPONSE extends ApiResponse> {

    /**
     * Execute the use case logic with authenticated user context.
     *
     * @param request The input request containing necessary data
     * @param user    The authenticated user making the request
     * @return The response DTO
     */
    RESPONSE execute(REQUEST request, AppUser user);
}
