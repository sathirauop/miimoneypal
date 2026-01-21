package com.sathira.miimoneypal.rest.categories.post;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository implementation for POST category operation.
 * Delegates to the shared CategoryDataAccess for database operations.
 */
@Repository
@RequiredArgsConstructor
public class PostCategoryRepository implements PostCategoryDataAccess {

    private final CategoryDataAccess categoryDataAccess;

    @Override
    public Category save(Category category) {
        return categoryDataAccess.save(category);
    }

    @Override
    public boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type) {
        return categoryDataAccess.existsByUserIdAndNameAndType(userId, name, type);
    }
}
