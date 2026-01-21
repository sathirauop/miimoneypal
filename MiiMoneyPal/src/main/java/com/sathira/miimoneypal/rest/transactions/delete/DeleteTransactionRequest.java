package com.sathira.miimoneypal.rest.transactions.delete;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for deleting a transaction by ID.
 * Used as input validation for DELETE /api/transactions/{id}.
 */
public record DeleteTransactionRequest(
        @NotNull(message = "Transaction ID is required")
        @Positive(message = "Transaction ID must be positive")
        Long id
) {}
