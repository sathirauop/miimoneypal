package com.sathira.miimoneypal.rest.categories.delete;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for deleting a category.
 */
public record DeleteCategoryRequest(
        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long id
) {
}
