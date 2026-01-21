package com.sathira.miimoneypal.rest.transactions.list;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing a single transaction in the list response.
 * Contains essential transaction details without deep nesting.
 */
public record TransactionSummary(
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
) {}
