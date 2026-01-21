package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.records.category.Category;

import java.util.Optional;

/**
 * Data access contract for Category entity.
 * Used by transaction endpoints to validate categories.
 */
public interface CategoryDataAccess {

    /**
     * Find category by ID (without user filtering).
     * Used when fetching related data for display.
     */
    Optional<Category> findById(Long id);

    /**
     * Find category by ID with user ownership validation.
     * Used when creating/updating transactions to ensure user owns the category.
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Check if category exists for user without fetching full record.
     * Efficient for validation-only scenarios.
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}
