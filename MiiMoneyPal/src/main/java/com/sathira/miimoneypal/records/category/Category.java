package com.sathira.miimoneypal.records.category;

import com.sathira.miimoneypal.records.transaction.TransactionType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain record representing a category for income or expense transactions.
 *
 * Categories are user-scoped (each user has their own categories).
 * System categories (is_system=true) cannot be deleted by users.
 * Archived categories are hidden from dropdowns but visible in transaction history.
 */
@Builder
public record Category(
        Long id,
        Long userId,
        String name,
        CategoryType type,
        String color,
        String icon,
        Boolean isSystem,
        Boolean isArchived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Compact constructor for validation.
     * Required fields: userId, name, type
     */
    public Category {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");

        // Default boolean fields to false if null
        if (isSystem == null) {
            isSystem = false;
        }
        if (isArchived == null) {
            isArchived = false;
        }
    }

    /**
     * Check if this category is protected (cannot be deleted).
     * System categories are protected.
     */
    public boolean isProtected() {
        return Boolean.TRUE.equals(isSystem);
    }

    /**
     * Check if this category is active (not archived).
     */
    public boolean isActive() {
        return !Boolean.TRUE.equals(isArchived);
    }

    /**
     * Check if this category can be used with the given transaction type.
     * INCOME categories can only be used with INCOME transactions.
     * EXPENSE categories can only be used with EXPENSE transactions.
     */
    public boolean canBeUsedWith(TransactionType transactionType) {
        return type.matchesTransactionType(transactionType);
    }

    /**
     * Check if this category can be deleted.
     * System categories cannot be deleted.
     * Note: Categories with existing transactions should be archived, not deleted.
     */
    public boolean canBeDeleted() {
        return !isProtected();
    }

    /**
     * Check if this category can be archived.
     * All non-archived categories can be archived.
     */
    public boolean canBeArchived() {
        return isActive();
    }
}
