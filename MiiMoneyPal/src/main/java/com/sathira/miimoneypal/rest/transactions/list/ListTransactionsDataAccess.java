package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access contract for listing transactions with filters.
 * Supports dynamic filtering and pagination.
 */
public interface ListTransactionsDataAccess {

    /**
     * Find transactions matching the specified filters with pagination.
     * Results are ordered by transaction_date DESC, created_at DESC.
     *
     * @param userId     owner user ID (enforces user-scoped queries)
     * @param type       optional transaction type filter
     * @param startDate  optional start date (inclusive)
     * @param endDate    optional end date (inclusive)
     * @param categoryId optional category filter
     * @param bucketId   optional bucket filter
     * @param searchTerm optional search term (searches in notes)
     * @param offset     pagination offset (0-based)
     * @param limit      pagination limit (page size)
     * @return list of transactions matching filters
     */
    List<Transaction> findByFilters(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long bucketId,
            String searchTerm,
            int offset,
            int limit
    );

    /**
     * Count total transactions matching the specified filters.
     * Used for pagination metadata.
     *
     * @param userId     owner user ID (enforces user-scoped queries)
     * @param type       optional transaction type filter
     * @param startDate  optional start date (inclusive)
     * @param endDate    optional end date (inclusive)
     * @param categoryId optional category filter
     * @param bucketId   optional bucket filter
     * @param searchTerm optional search term (searches in notes)
     * @return total count of transactions matching filters
     */
    long countByFilters(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long bucketId,
            String searchTerm
    );
}
