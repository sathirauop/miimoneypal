package com.sathira.miimoneypal.records.transaction;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain record representing a financial transaction.
 *
 * Transactions represent money movement:
 * - INCOME: Money coming in (requires category, affects usable amount +)
 * - EXPENSE: Money going out (requires category, affects usable amount -)
 * - INVESTMENT: Money transferred to bucket (requires bucket, affects usable amount -, bucket +)
 * - WITHDRAWAL: Money taken from bucket (requires bucket, affects usable amount +, bucket -)
 * - GOAL_COMPLETED: System-generated when "Mark as Spent" (requires bucket, bucket -)
 */
@Builder
public record Transaction(
        Long id,
        Long userId,
        TransactionType type,
        BigDecimal amount,
        LocalDate transactionDate,
        Long categoryId,
        Long bucketId,
        String note,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Maximum length for transaction notes.
     */
    public static final int MAX_NOTE_LENGTH = 500;

    /**
     * Compact constructor for validation.
     * Required fields: userId, type, amount, transactionDate
     */
    public Transaction {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(transactionDate, "transactionDate must not be null");

        // Amount must be positive (direction is determined by type)
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }

        // Validate category requirement
        if (type.requiresCategory() && categoryId == null) {
            throw new IllegalArgumentException(
                    type + " transactions require a categoryId");
        }

        // Validate bucket requirement
        if (type.requiresBucket() && bucketId == null) {
            throw new IllegalArgumentException(
                    type + " transactions require a bucketId");
        }

        // Category and bucket are mutually exclusive based on type
        if (type.requiresCategory() && bucketId != null) {
            throw new IllegalArgumentException(
                    type + " transactions should not have a bucketId");
        }
        if (type.requiresBucket() && categoryId != null) {
            throw new IllegalArgumentException(
                    type + " transactions should not have a categoryId");
        }

        // Validate note length
        if (note != null && note.length() > MAX_NOTE_LENGTH) {
            throw new IllegalArgumentException(
                    "note exceeds maximum length of " + MAX_NOTE_LENGTH);
        }
    }

    /**
     * Calculate the effect on usable amount.
     * Returns the signed amount (positive for increases, negative for decreases).
     */
    public BigDecimal usableAmountEffect() {
        return amount.multiply(BigDecimal.valueOf(type.usableAmountEffect()));
    }

    /**
     * Calculate the effect on bucket balance.
     * Returns the signed amount (positive for increases, negative for decreases).
     * Returns zero if this transaction doesn't affect bucket balance.
     */
    public BigDecimal bucketBalanceEffect() {
        return amount.multiply(BigDecimal.valueOf(type.bucketBalanceEffect()));
    }

    /**
     * Check if this transaction affects the usable amount.
     */
    public boolean affectsUsableAmount() {
        return type.usableAmountEffect() != 0;
    }

    /**
     * Check if this transaction affects a bucket balance.
     */
    public boolean affectsBucketBalance() {
        return type.bucketBalanceEffect() != 0;
    }

    /**
     * Check if this is an income or expense transaction (categorized).
     */
    public boolean isCategorized() {
        return type.requiresCategory();
    }

    /**
     * Check if this is a bucket-related transaction.
     */
    public boolean isBucketTransaction() {
        return type.requiresBucket();
    }

    /**
     * Check if this transaction was system-generated.
     * System-generated transactions cannot be edited or deleted by users.
     */
    public boolean isSystemGenerated() {
        return type.isSystemGenerated();
    }

    /**
     * Check if this transaction can be edited by the user.
     * System-generated transactions cannot be edited.
     */
    public boolean canBeEdited() {
        return !isSystemGenerated();
    }

    /**
     * Check if this transaction can be deleted by the user.
     * System-generated transactions cannot be deleted.
     */
    public boolean canBeDeleted() {
        return !isSystemGenerated();
    }

    /**
     * Check if this transaction increases the usable amount.
     */
    public boolean increasesUsableAmount() {
        return type.usableAmountEffect() > 0;
    }

    /**
     * Check if this transaction decreases the usable amount.
     */
    public boolean decreasesUsableAmount() {
        return type.usableAmountEffect() < 0;
    }

    /**
     * Check if this transaction has a note.
     */
    public boolean hasNote() {
        return note != null && !note.isBlank();
    }
}
