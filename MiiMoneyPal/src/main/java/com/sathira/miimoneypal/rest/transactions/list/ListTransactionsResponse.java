package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.models.response.ApiResponse;
import com.sathira.miimoneypal.models.response.OffsetSearchResponse;

/**
 * Response DTO for listing transactions with pagination.
 * Type alias for OffsetSearchResponse<TransactionSummary> to improve readability.
 */
public record ListTransactionsResponse(
        java.util.List<TransactionSummary> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) implements ApiResponse {
    /**
     * Create response from items and pagination parameters.
     * Converts offset/limit to page/size terminology.
     *
     * @param items      list of transaction summaries
     * @param offset     pagination offset (0-based)
     * @param limit      pagination limit (page size)
     * @param totalItems total number of items matching the filters
     * @return paginated response
     */
    public static ListTransactionsResponse of(
            java.util.List<TransactionSummary> items,
            int offset,
            int limit,
            long totalItems
    ) {
        int page = limit > 0 ? offset / limit : 0;
        int totalPages = limit > 0 ? (int) Math.ceil((double) totalItems / limit) : 0;
        boolean hasNext = offset + limit < totalItems;
        boolean hasPrevious = offset > 0;

        return new ListTransactionsResponse(
                items,
                page,
                limit,
                totalItems,
                totalPages,
                hasNext,
                hasPrevious
        );
    }

    /**
     * Convert to generic OffsetSearchResponse.
     */
    public OffsetSearchResponse<TransactionSummary> toOffsetSearchResponse() {
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
