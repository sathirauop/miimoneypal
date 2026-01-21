package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.records.transaction.TransactionType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a new transaction.
 * Validates basic constraints; business rules are validated in UseCase.
 */
public record PostTransactionRequest(
        @NotNull(message = "Transaction type is required")
        TransactionType type,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        @Digits(integer = 13, fraction = 2, message = "Amount must have at most 13 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate transactionDate,

        Long categoryId,  // Required for INCOME/EXPENSE (validated in UseCase)

        Long bucketId,    // Required for INVESTMENT/WITHDRAWAL (validated in UseCase)

        @Size(max = 500, message = "Note must not exceed 500 characters")
        String note
) {}
