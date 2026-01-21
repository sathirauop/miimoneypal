package com.sathira.miimoneypal.rest.categories.delete;

import com.sathira.miimoneypal.records.category.Category;

import java.util.Optional;

/**
 * Data access interface for DELETE category operation.
 * Handles both soft delete (archive) and hard delete based on business rules.
 */
public interface DeleteCategoryDataAccess {

    /**
     * Find category by ID with user ownership validation.
     * @param id The category ID
     * @param userId The user ID
     * @return Optional containing the category if found and owned by user
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Check if category has associated transactions.
     * @param categoryId The category ID
     * @return true if transactions exist, false otherwise
     */
    boolean hasTransactions(Long categoryId);

    /**
     * Hard delete a category by ID.
     * Should only be called when the category has no transactions.
     * @param id The category ID
     */
    void hardDelete(Long id);

    /**
     * Soft delete (archive) a category by ID.
     * Sets is_archived = true instead of deleting the record.
     * @param id The category ID
     */
    void softDelete(Long id);
}
