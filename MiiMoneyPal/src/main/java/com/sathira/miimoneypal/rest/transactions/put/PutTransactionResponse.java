package com.sathira.miimoneypal.rest.transactions.put;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for successful transaction update.
 * Returns full transaction details including related entity names.
 */
public record PutTransactionResponse(
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
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements ApiResponse {}
