package com.sathira.miimoneypal.rest.categories.get;

import com.sathira.miimoneypal.records.category.Category;
import org.springframework.stereotype.Component;

/**
 * Presenter for GET category operation.
 * Handles transformation from domain Category to response DTO.
 */
@Component
public class GetCategoryPresenter implements GetCategoryResponseBuilder {

    @Override
    public GetCategoryResponse build(Category category) {
        return new GetCategoryResponse(
                category.id(),
                category.name(),
                category.type().name(),
                category.color(),
                category.icon(),
                category.isSystem(),
                category.isArchived(),
                category.createdAt(),
                category.updatedAt()
        );
    }
}
