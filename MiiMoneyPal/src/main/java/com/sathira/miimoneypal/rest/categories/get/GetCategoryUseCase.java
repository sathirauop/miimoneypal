package com.sathira.miimoneypal.rest.categories.get;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for retrieving a single category by ID.
 *
 * Business Rules:
 * - User can only access their own categories
 * - Returns 404 if category not found or doesn't belong to user
 */
@Service
@RequiredArgsConstructor
public class GetCategoryUseCase implements AuthenticatedUseCase<GetCategoryRequest, GetCategoryResponse> {

    private final GetCategoryDataAccess dataAccess;
    private final GetCategoryResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public GetCategoryResponse execute(GetCategoryRequest request, AppUser user) {
        // Find category with user ownership validation
        Category category = dataAccess.findByIdAndUserId(request.id(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Transform to response DTO
        return responseBuilder.build(category);
    }
}
