package com.sathira.miimoneypal.models.response;

import java.util.List;

/**
 * Generic paginated response using offset-based pagination.
 * Used for list endpoints that return multiple items.
 *
 * @param <T> The type of items in the response
 */
public record OffsetSearchResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) implements ApiResponse {

    /**
     * Create response from items and pagination parameters.
     */
    public static <T> OffsetSearchResponse<T> of(
            List<T> items,
            int page,
            int size,
            long totalItems
    ) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalItems / size) : 0;
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return new OffsetSearchResponse<>(
                items,
                page,
                size,
                totalItems,
                totalPages,
                hasNext,
                hasPrevious
        );
    }
}
