package com.sathira.miimoneypal.rest.transactions.get;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for fetching a single transaction by ID.
 * Used as input validation for GET /api/transactions/{id}.
 */
public record GetTransactionRequest(
        @NotNull(message = "Transaction ID is required")
        @Positive(message = "Transaction ID must be positive")
        Long id
) {}
