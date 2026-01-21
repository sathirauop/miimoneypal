package com.sathira.miimoneypal.rest.categories.put;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository implementation for PUT category operation.
 * Delegates to the shared CategoryDataAccess for database operations.
 */
@Repository
@RequiredArgsConstructor
public class PutCategoryRepository implements PutCategoryDataAccess {

    private final CategoryDataAccess categoryDataAccess;

    @Override
    public Optional<Category> findByIdAndUserId(Long id, Long userId) {
        return categoryDataAccess.findByIdAndUserId(id, userId);
    }

    @Override
    public boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type) {
        return categoryDataAccess.existsByUserIdAndNameAndType(userId, name, type);
    }

    @Override
    public Category update(Category category) {
        return categoryDataAccess.update(category);
    }
}
