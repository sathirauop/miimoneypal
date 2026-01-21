package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.records.category.Category;
import org.springframework.stereotype.Component;

/**
 * Presenter for POST category operation.
 * Handles transformation from domain Category to response DTO.
 */
@Component
public class PostCategoryPresenter implements PostCategoryResponseBuilder {

    @Override
    public PostCategoryResponse build(Category category) {
        return new PostCategoryResponse(
                category.id(),
                category.name(),
                category.type().name(),
                category.color(),
                category.icon(),
                category.isSystem(),
                category.isArchived(),
                category.createdAt()
        );
    }
}
