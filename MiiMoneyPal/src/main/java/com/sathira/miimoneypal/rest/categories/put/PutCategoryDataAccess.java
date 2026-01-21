package com.sathira.miimoneypal.rest.categories.put;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;

import java.util.Optional;

/**
 * Data access interface for PUT category operation.
 * Handles category update operations.
 */
public interface PutCategoryDataAccess {

    /**
     * Find category by ID with user ownership validation.
     * @param id The category ID
     * @param userId The user ID
     * @return Optional containing the category if found and owned by user
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    /**
     * Check if a category with the given name and type already exists for the user.
     * Used to enforce unique category names when renaming.
     * @param userId The user ID
     * @param name The category name
     * @param type The category type
     * @return true if a category exists, false otherwise
     */
    boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type);

    /**
     * Update an existing category.
     * @param category The category with updated fields
     * @return The updated category
     */
    Category update(Category category);
}
