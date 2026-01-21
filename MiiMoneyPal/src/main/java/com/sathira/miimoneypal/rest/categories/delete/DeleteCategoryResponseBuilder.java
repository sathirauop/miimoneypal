package com.sathira.miimoneypal.rest.categories.delete;

/**
 * Response builder interface for DELETE category operation.
 * Builds response based on deletion type (archived vs deleted).
 */
public interface DeleteCategoryResponseBuilder {

    /**
     * Build a response for archived category.
     * @param categoryId The deleted category ID
     * @return The response DTO
     */
    DeleteCategoryResponse buildArchivedResponse(Long categoryId);

    /**
     * Build a response for hard deleted category.
     * @param categoryId The deleted category ID
     * @return The response DTO
     */
    DeleteCategoryResponse buildDeletedResponse(Long categoryId);
}
