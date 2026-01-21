package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.records.category.Category;

/**
 * Response builder interface for POST category operation.
 * Transforms domain Category record to PostCategoryResponse DTO.
 */
public interface PostCategoryResponseBuilder {

    /**
     * Build a response DTO from a Category domain record.
     * @param category The domain category record
     * @return The response DTO
     */
    PostCategoryResponse build(Category category);
}
