package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;

/**
 * Data access interface for POST category operation.
 * Defines the database operations needed to create a category.
 */
public interface PostCategoryDataAccess {

    /**
     * Save a new category to the database.
     * @param category The category to save
     * @return The saved category with generated ID
     */
    Category save(Category category);

    /**
     * Check if a category with the given name and type already exists for the user.
     * @param userId The user ID
     * @param name The category name
     * @param type The category type
     * @return true if a category exists, false otherwise
     */
    boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type);
}
