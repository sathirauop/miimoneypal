package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for successful transaction creation.
 * Returns full transaction details including related entity names.
 */
public record PostTransactionResponse(
        Long id,
        String type,
        BigDecimal amount,
        String formattedAmount,
        LocalDate transactionDate,
        Long categoryId,
        String categoryName,
        Long bucketId,
        String bucketName,
        String note,
        LocalDateTime createdAt
) implements ApiResponse {}
