package com.sathira.miimoneypal.rest.categories.get;

import com.sathira.miimoneypal.records.category.Category;

/**
 * Response builder interface for GET category operation.
 * Transforms domain Category record to GetCategoryResponse DTO.
 */
public interface GetCategoryResponseBuilder {

    /**
     * Build a response DTO from a Category domain record.
     * @param category The domain category record
     * @return The response DTO
     */
    GetCategoryResponse build(Category category);
}
