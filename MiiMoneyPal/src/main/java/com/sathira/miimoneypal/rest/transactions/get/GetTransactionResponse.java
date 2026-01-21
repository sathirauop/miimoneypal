package com.sathira.miimoneypal.rest.transactions.get;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for fetching a single transaction.
 * Returns full transaction details including related entity information.
 */
public record GetTransactionResponse(
        Long id,
        String type,
        BigDecimal amount,
        String formattedAmount,
        LocalDate transactionDate,
        Long categoryId,
        String categoryName,
        String categoryType,      // INCOME or EXPENSE
        Long bucketId,
        String bucketName,
        String bucketType,        // SAVINGS_GOAL or PERPETUAL_ASSET
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements ApiResponse {}
