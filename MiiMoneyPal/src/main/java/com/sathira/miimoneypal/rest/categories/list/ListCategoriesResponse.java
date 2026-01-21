package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.models.response.ApiResponse;

import java.util.List;

/**
 * Response DTO for listing categories.
 * Contains a list of category summaries and total count.
 */
public record ListCategoriesResponse(
        List<CategorySummary> categories,
        int total
) implements ApiResponse {
}
