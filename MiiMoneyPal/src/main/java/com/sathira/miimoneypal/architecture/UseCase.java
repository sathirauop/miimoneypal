package com.sathira.miimoneypal.architecture;

import com.sathira.miimoneypal.models.response.ApiResponse;

/**
 * Base interface for all use cases (business logic orchestrators).
 * Use cases receive a request, execute business logic, and return a response.
 *
 * @param <REQUEST>  The input request type (usually a record with @Valid annotations)
 * @param <RESPONSE> The output response type (must implement ApiResponse)
 */
public interface UseCase<REQUEST, RESPONSE extends ApiResponse> {

    /**
     * Execute the use case logic.
     *
     * @param request The input request containing necessary data
     * @return The response DTO
     */
    RESPONSE execute(REQUEST request);
}
