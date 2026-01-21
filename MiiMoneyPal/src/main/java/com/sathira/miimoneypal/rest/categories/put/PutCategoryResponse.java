package com.sathira.miimoneypal.rest.categories.put;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.time.LocalDateTime;

/**
 * Response DTO for category update operation.
 * Returns the updated category with all its fields.
 */
public record PutCategoryResponse(
        Long id,
        String name,
        String type,
        String color,
        String icon,
        Boolean isSystem,
        Boolean isArchived,
        LocalDateTime updatedAt
) implements ApiResponse {
}
