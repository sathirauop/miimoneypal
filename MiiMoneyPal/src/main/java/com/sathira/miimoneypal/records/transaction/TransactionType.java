package com.sathira.miimoneypal.records.transaction;

/**
 * Types of financial transactions in MiiMoneyPal.
 *
 * User-created types: INCOME, EXPENSE, INVESTMENT, WITHDRAWAL
 * System-generated types: GOAL_COMPLETED (created by "Mark as Spent" action)
 */
public enum TransactionType {
    INCOME,
    EXPENSE,
    INVESTMENT,
    WITHDRAWAL,
    GOAL_COMPLETED;

    /**
     * Check if this transaction type requires a category.
     * INCOME and EXPENSE transactions must have a category.
     */
    public boolean requiresCategory() {
        return this == INCOME || this == EXPENSE;
    }

    /**
     * Check if this transaction type requires a bucket.
     * INVESTMENT, WITHDRAWAL, and GOAL_COMPLETED transactions must have a bucket.
     */
    public boolean requiresBucket() {
        return this == INVESTMENT || this == WITHDRAWAL || this == GOAL_COMPLETED;
    }

    /**
     * Check if this is a system-generated transaction type.
     * Users cannot manually create GOAL_COMPLETED transactions.
     */
    public boolean isSystemGenerated() {
        return this == GOAL_COMPLETED;
    }

    /**
     * Get the effect on usable amount.
     * @return 1 for increase, -1 for decrease, 0 for no effect
     */
    public int usableAmountEffect() {
        return switch (this) {
            case INCOME, WITHDRAWAL -> 1;       // Increases usable amount
            case EXPENSE, INVESTMENT -> -1;     // Decreases usable amount
            case GOAL_COMPLETED -> 0;           // No effect (money left system)
        };
    }

    /**
     * Get the effect on bucket balance.
     * @return 1 for increase, -1 for decrease, 0 for no effect
     */
    public int bucketBalanceEffect() {
        return switch (this) {
            case INVESTMENT -> 1;                       // Increases bucket balance
            case WITHDRAWAL, GOAL_COMPLETED -> -1;      // Decreases bucket balance
            case INCOME, EXPENSE -> 0;                  // No effect on buckets
        };
    }
}
