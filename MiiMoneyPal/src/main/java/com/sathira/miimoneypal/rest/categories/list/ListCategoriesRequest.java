package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.records.category.CategoryType;

/**
 * Request DTO for listing categories with optional filters.
 */
public record ListCategoriesRequest(
        CategoryType type,
        Boolean includeArchived
) {
    /**
     * Compact constructor with default value for includeArchived.
     */
    public ListCategoriesRequest {
        if (includeArchived == null) {
            includeArchived = false;
        }
    }
}
