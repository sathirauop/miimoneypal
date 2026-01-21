package com.sathira.miimoneypal.rest.categories.delete;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.BusinessRuleException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for deleting a category.
 *
 * Business Rules:
 * - User can only delete their own categories
 * - System categories cannot be deleted
 * - Soft delete (archive) if transactions exist
 * - Hard delete if no transactions exist
 */
@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCase implements AuthenticatedUseCase<DeleteCategoryRequest, DeleteCategoryResponse> {

    private final DeleteCategoryDataAccess dataAccess;
    private final DeleteCategoryResponseBuilder responseBuilder;

    @Override
    @Transactional
    public DeleteCategoryResponse execute(DeleteCategoryRequest request, AppUser user) {
        // Find category with user ownership validation
        Category category = dataAccess.findByIdAndUserId(request.id(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Prevent deleting system categories
        if (category.isProtected()) {
            throw new BusinessRuleException("System categories cannot be deleted");
        }

        // Check if category has transactions
        boolean hasTransactions = dataAccess.hasTransactions(request.id());

        if (hasTransactions) {
            // Soft delete (archive) if transactions exist
            dataAccess.softDelete(request.id());
            return responseBuilder.buildArchivedResponse(request.id());
        } else {
            // Hard delete if no transactions
            dataAccess.hardDelete(request.id());
            return responseBuilder.buildDeletedResponse(request.id());
        }
    }
}
