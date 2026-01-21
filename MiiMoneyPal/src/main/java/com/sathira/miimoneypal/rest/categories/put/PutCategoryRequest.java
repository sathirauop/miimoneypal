package com.sathira.miimoneypal.rest.categories.put;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a category.
 * Note: Category type cannot be changed after creation.
 */
public record PutCategoryRequest(
        @NotNull(message = "ID is required")
        @Positive(message = "ID must be positive")
        Long id,

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @Size(max = 20, message = "Color must not exceed 20 characters")
        String color,

        @Size(max = 50, message = "Icon must not exceed 50 characters")
        String icon
) {
}
