package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository implementation for LIST categories operation.
 * Delegates to the shared CategoryDataAccess for database operations.
 */
@Repository
@RequiredArgsConstructor
public class ListCategoriesRepository implements ListCategoriesDataAccess {

    private final CategoryDataAccess categoryDataAccess;

    @Override
    public List<Category> findAllByUserId(Long userId, CategoryType type, boolean includeArchived) {
        return categoryDataAccess.findAllByUserId(userId, type, includeArchived);
    }
}
