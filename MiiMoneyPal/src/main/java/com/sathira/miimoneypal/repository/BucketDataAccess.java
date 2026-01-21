package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.records.bucket.Bucket;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Data access contract for Bucket entity.
 * Used by transaction endpoints to validate buckets and calculate balances.
 */
public interface BucketDataAccess {

    /**
     * Find bucket by ID (without user filtering).
     * Used when fetching related data for display.
     */
    Optional<Bucket> findById(Long id);

    /**
     * Find bucket by ID with user ownership validation.
     * Used when creating/updating transactions to ensure user owns the bucket.
     */
    Optional<Bucket> findByIdAndUserId(Long id, Long userId);

    /**
     * Check if bucket exists for user without fetching full record.
     * Efficient for validation-only scenarios.
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Calculate current bucket balance by summing all transactions.
     * INVESTMENT transactions increase balance (+)
     * WITHDRAWAL transactions decrease balance (-)
     * GOAL_COMPLETED transactions decrease balance (-)
     */
    BigDecimal calculateBalance(Long bucketId);
}
