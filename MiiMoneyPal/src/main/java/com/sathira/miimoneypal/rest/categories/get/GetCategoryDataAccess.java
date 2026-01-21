package com.sathira.miimoneypal.rest.categories.get;

import com.sathira.miimoneypal.records.category.Category;

import java.util.Optional;

/**
 * Data access interface for GET category operation.
 * Retrieves a single category with user ownership validation.
 */
public interface GetCategoryDataAccess {

    /**
     * Find category by ID with user ownership validation.
     * @param id The category ID
     * @param userId The user ID
     * @return Optional containing the category if found and owned by user
     */
    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
