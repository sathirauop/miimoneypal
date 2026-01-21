package com.sathira.miimoneypal.rest.categories.delete;

import com.sathira.miimoneypal.models.response.ApiResponse;

/**
 * Response DTO for category deletion.
 * Indicates whether category was archived or hard deleted.
 */
public record DeleteCategoryResponse(
        String message,
        Long deletedCategoryId,
        String deletionType  // "ARCHIVED" or "DELETED"
) implements ApiResponse {
}
