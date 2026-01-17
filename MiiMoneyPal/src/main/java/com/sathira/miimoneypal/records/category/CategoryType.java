package com.sathira.miimoneypal.records.category;

import com.sathira.miimoneypal.records.transaction.TransactionType;

/**
 * Types of categories for income and expense transactions.
 * Categories are typed to ensure proper matching with transaction types.
 */
public enum CategoryType {
    INCOME,
    EXPENSE;

    /**
     * Check if this category type matches the given transaction type.
     * INCOME categories can only be used with INCOME transactions.
     * EXPENSE categories can only be used with EXPENSE transactions.
     */
    public boolean matchesTransactionType(TransactionType transactionType) {
        return switch (this) {
            case INCOME -> transactionType == TransactionType.INCOME;
            case EXPENSE -> transactionType == TransactionType.EXPENSE;
        };
    }
}
