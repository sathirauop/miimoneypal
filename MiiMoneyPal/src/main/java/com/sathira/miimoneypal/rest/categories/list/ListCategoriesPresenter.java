package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.records.category.Category;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Presenter for LIST categories operation.
 * Handles transformation from domain Category list to response DTO.
 */
@Component
public class ListCategoriesPresenter implements ListCategoriesResponseBuilder {

    @Override
    public ListCategoriesResponse build(List<Category> categories) {
        List<CategorySummary> summaries = categories.stream()
                .map(this::toCategorySummary)
                .toList();

        return new ListCategoriesResponse(summaries, summaries.size());
    }

    private CategorySummary toCategorySummary(Category category) {
        return new CategorySummary(
                category.id(),
                category.name(),
                category.type().name(),
                category.color(),
                category.icon(),
                category.isSystem(),
                category.isArchived()
        );
    }
}
