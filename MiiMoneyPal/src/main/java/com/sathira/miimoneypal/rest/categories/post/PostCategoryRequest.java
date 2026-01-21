package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.records.category.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new category.
 */
public record PostCategoryRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotNull(message = "Type is required")
        CategoryType type,

        @Size(max = 20, message = "Color must not exceed 20 characters")
        String color,

        @Size(max = 50, message = "Icon must not exceed 50 characters")
        String icon
) {
}
