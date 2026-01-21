package com.sathira.miimoneypal.rest.categories.put;

import com.sathira.miimoneypal.records.category.Category;

/**
 * Response builder interface for PUT category operation.
 * Transforms domain Category record to PutCategoryResponse DTO.
 */
public interface PutCategoryResponseBuilder {

    /**
     * Build a response DTO from a Category domain record.
     * @param category The domain category record
     * @return The response DTO
     */
    PutCategoryResponse build(Category category);
}
