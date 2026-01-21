package com.sathira.miimoneypal.rest.transactions.put;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating a transaction.
 * Transaction type cannot be changed after creation.
 * Used as input validation for PUT /api/transactions/{id}.
 */
public record PutTransactionRequest(
        @NotNull(message = "Transaction ID is required")
        @Positive(message = "Transaction ID must be positive")
        Long id,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        @Digits(integer = 13, fraction = 2, message = "Amount must have at most 13 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate transactionDate,

        Long categoryId,  // Required for INCOME/EXPENSE, must be null for INVESTMENT/WITHDRAWAL

        Long bucketId,    // Required for INVESTMENT/WITHDRAWAL, must be null for INCOME/EXPENSE

        @Size(max = 500, message = "Note must not exceed 500 characters")
        String note
) {}
