package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;

import java.util.List;
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

    /**
     * Check if a category with the given name and type already exists for the user.
     * Used to enforce unique category names per user per type.
     */
    boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type);

    /**
     * Check if a category has associated transactions.
     * Used to determine whether to soft delete (archive) or hard delete.
     */
    boolean hasTransactions(Long categoryId);

    /**
     * Find all categories for a user with optional filters.
     * @param userId The user ID
     * @param type Optional filter by category type (null = all types)
     * @param includeArchived Whether to include archived categories
     * @return List of categories ordered by name
     */
    List<Category> findAllByUserId(Long userId, CategoryType type, boolean includeArchived);

    /**
     * Save a new category.
     * @param category The category to save
     * @return The saved category with generated ID
     */
    Category save(Category category);

    /**
     * Update an existing category.
     * @param category The category with updated fields
     * @return The updated category
     */
    Category update(Category category);

    /**
     * Hard delete a category by ID.
     * Should only be called when the category has no transactions.
     */
    void deleteById(Long id);

    /**
     * Soft delete (archive) a category by ID.
     * Sets is_archived = true instead of deleting the record.
     */
    void archive(Long id);
}
