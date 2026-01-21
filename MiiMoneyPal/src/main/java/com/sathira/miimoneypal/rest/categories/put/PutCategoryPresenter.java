package com.sathira.miimoneypal.rest.categories.put;

import com.sathira.miimoneypal.records.category.Category;
import org.springframework.stereotype.Component;

/**
 * Presenter for PUT category operation.
 * Handles transformation from domain Category to response DTO.
 */
@Component
public class PutCategoryPresenter implements PutCategoryResponseBuilder {

    @Override
    public PutCategoryResponse build(Category category) {
        return new PutCategoryResponse(
                category.id(),
                category.name(),
                category.type().name(),
                category.color(),
                category.icon(),
                category.isSystem(),
                category.isArchived(),
                category.updatedAt()
        );
    }
}
