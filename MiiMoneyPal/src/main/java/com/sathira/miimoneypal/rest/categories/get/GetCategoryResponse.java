package com.sathira.miimoneypal.rest.categories.get;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.time.LocalDateTime;

/**
 * Response DTO for retrieving a single category.
 * Includes all category fields including updatedAt timestamp.
 */
public record GetCategoryResponse(
        Long id,
        String name,
        String type,
        String color,
        String icon,
        Boolean isSystem,
        Boolean isArchived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements ApiResponse {
}
