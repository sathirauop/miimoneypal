package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.time.LocalDateTime;

/**
 * Response DTO for category creation.
 * Returns the created category with all its fields.
 */
public record PostCategoryResponse(
        Long id,
        String name,
        String type,
        String color,
        String icon,
        Boolean isSystem,
        Boolean isArchived,
        LocalDateTime createdAt
) implements ApiResponse {
}
