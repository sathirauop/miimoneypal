package com.sathira.miimoneypal.records.bucket;

/**
 * Types of buckets for tracking savings and investments.
 *
 * SAVINGS_GOAL: Has optional target amount, can be "marked as spent" when goal is achieved.
 * PERPETUAL_ASSET: No target amount, represents ongoing investments (e.g., Unit Trusts).
 */
public enum BucketType {
    SAVINGS_GOAL,
    PERPETUAL_ASSET;

    /**
     * Check if this bucket type can have a target amount.
     * Only SAVINGS_GOAL buckets can have targets.
     */
    public boolean canHaveTarget() {
        return this == SAVINGS_GOAL;
    }

    /**
     * Check if this bucket type supports the "Mark as Spent" action.
     * Only SAVINGS_GOAL buckets can be marked as spent.
     */
    public boolean supportsMarkAsSpent() {
        return this == SAVINGS_GOAL;
    }
}
