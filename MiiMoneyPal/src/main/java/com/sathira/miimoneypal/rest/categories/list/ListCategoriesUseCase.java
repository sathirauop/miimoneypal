package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case for listing categories with optional filters.
 *
 * Business Rules:
 * - Returns only user's own categories
 * - Can filter by category type (INCOME/EXPENSE)
 * - Archived categories excluded by default (unless includeArchived=true)
 * - Results ordered alphabetically by name
 */
@Service
@RequiredArgsConstructor
public class ListCategoriesUseCase implements AuthenticatedUseCase<ListCategoriesRequest, ListCategoriesResponse> {

    private final ListCategoriesDataAccess dataAccess;
    private final ListCategoriesResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public ListCategoriesResponse execute(ListCategoriesRequest request, AppUser user) {
        // Fetch categories with filters
        List<Category> categories = dataAccess.findAllByUserId(
                user.getId(),
                request.type(),
                request.includeArchived()
        );

        // Transform to response DTO
        return responseBuilder.build(categories);
    }
}
