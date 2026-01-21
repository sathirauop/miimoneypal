package com.sathira.miimoneypal.rest.categories.delete;

import org.springframework.stereotype.Component;

/**
 * Presenter for DELETE category operation.
 * Builds appropriate response messages for archived or deleted categories.
 */
@Component
public class DeleteCategoryPresenter implements DeleteCategoryResponseBuilder {

    @Override
    public DeleteCategoryResponse buildArchivedResponse(Long categoryId) {
        return new DeleteCategoryResponse(
                "Category has existing transactions and has been archived",
                categoryId,
                "ARCHIVED"
        );
    }

    @Override
    public DeleteCategoryResponse buildDeletedResponse(Long categoryId) {
        return new DeleteCategoryResponse(
                "Category deleted successfully",
                categoryId,
                "DELETED"
        );
    }
}
