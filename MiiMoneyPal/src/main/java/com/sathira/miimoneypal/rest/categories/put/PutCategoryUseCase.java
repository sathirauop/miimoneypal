package com.sathira.miimoneypal.rest.categories.put;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.BusinessRuleException;
import com.sathira.miimoneypal.exception.DuplicateResourceException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for updating a category.
 *
 * Business Rules:
 * - User can only update their own categories
 * - System categories cannot be updated
 * - Type is immutable (cannot be changed after creation)
 * - Name must be unique per user per type (if renaming)
 * - Name is normalized (trimmed)
 * - Cannot un-archive via update (separate endpoint if needed)
 */
@Service
@RequiredArgsConstructor
public class PutCategoryUseCase implements AuthenticatedUseCase<PutCategoryRequest, PutCategoryResponse> {

    private final PutCategoryDataAccess dataAccess;
    private final PutCategoryResponseBuilder responseBuilder;

    @Override
    @Transactional
    public PutCategoryResponse execute(PutCategoryRequest request, AppUser user) {
        // Find existing category with user ownership validation
        Category existingCategory = dataAccess.findByIdAndUserId(request.id(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Prevent updating system categories
        if (existingCategory.isProtected()) {
            throw new BusinessRuleException("System categories cannot be updated");
        }

        // Normalize the name
        String normalizedName = request.name().trim();

        // Check for duplicate name if renaming
        if (!existingCategory.name().equals(normalizedName)) {
            if (dataAccess.existsByUserIdAndNameAndType(user.getId(), normalizedName, existingCategory.type())) {
                throw new DuplicateResourceException(
                        "Category with name '" + normalizedName + "' and type '" + existingCategory.type() + "' already exists"
                );
            }
        }

        // Build updated category (type and system flags are immutable)
        Category updatedCategory = Category.builder()
                .id(existingCategory.id())
                .userId(existingCategory.userId())
                .name(normalizedName)
                .type(existingCategory.type())  // Type is immutable
                .color(request.color())
                .icon(request.icon())
                .isSystem(existingCategory.isSystem())  // System flag is immutable
                .isArchived(existingCategory.isArchived())  // Archive state not changed via update
                .createdAt(existingCategory.createdAt())
                .build();

        // Update in database
        Category savedCategory = dataAccess.update(updatedCategory);

        // Transform to response DTO
        return responseBuilder.build(savedCategory);
    }
}
