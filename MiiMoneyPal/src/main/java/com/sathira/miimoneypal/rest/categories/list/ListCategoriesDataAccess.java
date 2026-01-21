package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;

import java.util.List;

/**
 * Data access interface for LIST categories operation.
 * Retrieves categories with optional filters.
 */
public interface ListCategoriesDataAccess {

    /**
     * Find all categories for a user with optional filters.
     * @param userId The user ID
     * @param type Optional filter by category type (null = all types)
     * @param includeArchived Whether to include archived categories
     * @return List of categories ordered by name
     */
    List<Category> findAllByUserId(Long userId, CategoryType type, boolean includeArchived);
}
