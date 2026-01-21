package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.records.transaction.TransactionType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request DTO for listing transactions with filters and pagination.
 * All filters are optional. Used as input for GET /api/transactions.
 */
public record ListTransactionsRequest(
        TransactionType type,           // Optional: filter by transaction type
        LocalDate startDate,            // Optional: filter transactions on or after this date
        LocalDate endDate,              // Optional: filter transactions on or before this date
        Long categoryId,                // Optional: filter by category
        Long bucketId,                  // Optional: filter by bucket

        @Size(max = 100, message = "Search term must not exceed 100 characters")
        String searchTerm,              // Optional: search in transaction notes

        @Min(value = 0, message = "Offset must be 0 or greater")
        Integer offset,                 // Pagination offset (default: 0)

        @Min(value = 1, message = "Limit must be at least 1")
        @Max(value = 100, message = "Limit must not exceed 100")
        Integer limit                   // Pagination limit (default: 20, max: 100)
) {
    /**
     * Constructor with default values for pagination.
     */
    public ListTransactionsRequest {
        offset = (offset != null) ? offset : 0;
        limit = (limit != null) ? limit : 20;
    }
}
