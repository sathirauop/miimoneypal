package com.sathira.miimoneypal.records.bucket;

/**
 * Status of a bucket.
 *
 * ACTIVE: Bucket is in use, can receive investments and withdrawals.
 * ARCHIVED: Bucket is completed or hidden, visible in "Completed Goals" section.
 */
public enum BucketStatus {
    ACTIVE,
    ARCHIVED;

    /**
     * Check if this status allows new transactions.
     * Only ACTIVE buckets can receive new investments or withdrawals.
     */
    public boolean allowsTransactions() {
        return this == ACTIVE;
    }
}
