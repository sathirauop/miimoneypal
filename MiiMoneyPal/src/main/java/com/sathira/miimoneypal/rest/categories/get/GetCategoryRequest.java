package com.sathira.miimoneypal.rest.categories.get;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for retrieving a single category by ID.
 */
public record GetCategoryRequest(
        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long id
) {
}
