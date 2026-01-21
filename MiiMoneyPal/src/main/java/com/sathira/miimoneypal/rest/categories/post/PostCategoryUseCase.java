package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.DuplicateResourceException;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for creating a new category.
 *
 * Business Rules:
 * - Category name must be unique per user per type
 * - Name is normalized (trimmed)
 * - Color and icon are optional
 * - System flag defaults to false
 * - Archived flag defaults to false
 */
@Service
@RequiredArgsConstructor
public class PostCategoryUseCase implements AuthenticatedUseCase<PostCategoryRequest, PostCategoryResponse> {

    private final PostCategoryDataAccess dataAccess;
    private final PostCategoryResponseBuilder responseBuilder;

    @Override
    @Transactional
    public PostCategoryResponse execute(PostCategoryRequest request, AppUser user) {
        // Normalize the name (trim whitespace)
        String normalizedName = request.name().trim();

        // Validate name uniqueness per user per type
        if (dataAccess.existsByUserIdAndNameAndType(user.getId(), normalizedName, request.type())) {
            throw new DuplicateResourceException(
                    "Category with name '" + normalizedName + "' and type '" + request.type() + "' already exists"
            );
        }

        // Build domain category record
        Category category = Category.builder()
                .userId(user.getId())
                .name(normalizedName)
                .type(request.type())
                .color(request.color())
                .icon(request.icon())
                .isSystem(false)
                .isArchived(false)
                .build();

        // Save to database
        Category savedCategory = dataAccess.save(category);

        // Transform to response DTO
        return responseBuilder.build(savedCategory);
    }
}
