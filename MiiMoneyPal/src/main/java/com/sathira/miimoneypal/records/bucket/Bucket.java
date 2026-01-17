package com.sathira.miimoneypal.records.bucket;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain record representing a bucket for tracking savings goals or perpetual assets.
 *
 * Buckets are user-scoped containers that hold invested money.
 * SAVINGS_GOAL buckets have optional target amounts and can be "marked as spent".
 * PERPETUAL_ASSET buckets represent ongoing investments with no target.
 */
@Builder
public record Bucket(
        Long id,
        Long userId,
        String name,
        BucketType type,
        BigDecimal targetAmount,
        BucketStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Compact constructor for validation.
     * Required fields: userId, name, type
     */
    public Bucket {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");

        // Default status to ACTIVE if null
        if (status == null) {
            status = BucketStatus.ACTIVE;
        }

        // Validate target amount only makes sense for SAVINGS_GOAL
        if (targetAmount != null && !type.canHaveTarget()) {
            throw new IllegalArgumentException(
                    "targetAmount can only be set for SAVINGS_GOAL buckets, not " + type);
        }

        // Ensure target amount is positive if provided
        if (targetAmount != null && targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("targetAmount must be positive");
        }
    }

    /**
     * Check if this bucket can have a target amount.
     * Only SAVINGS_GOAL buckets support targets.
     */
    public boolean canHaveTarget() {
        return type.canHaveTarget();
    }

    /**
     * Check if this bucket supports the "Mark as Spent" action.
     * Only SAVINGS_GOAL buckets can be marked as spent.
     */
    public boolean canMarkAsSpent() {
        return type.supportsMarkAsSpent() && status.allowsTransactions();
    }

    /**
     * Check if this bucket can receive new transactions (investments/withdrawals).
     * Only ACTIVE buckets can receive transactions.
     */
    public boolean canReceiveTransactions() {
        return status.allowsTransactions();
    }

    /**
     * Check if this bucket is active.
     */
    public boolean isActive() {
        return status == BucketStatus.ACTIVE;
    }

    /**
     * Check if this bucket is archived.
     */
    public boolean isArchived() {
        return status == BucketStatus.ARCHIVED;
    }

    /**
     * Check if this bucket has a target amount set.
     */
    public boolean hasTarget() {
        return targetAmount != null;
    }

    /**
     * Check if this bucket is a savings goal type.
     */
    public boolean isSavingsGoal() {
        return type == BucketType.SAVINGS_GOAL;
    }

    /**
     * Check if this bucket is a perpetual asset type.
     */
    public boolean isPerpetualAsset() {
        return type == BucketType.PERPETUAL_ASSET;
    }
}
