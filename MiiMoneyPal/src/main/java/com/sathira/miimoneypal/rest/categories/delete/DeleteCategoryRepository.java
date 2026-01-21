package com.sathira.miimoneypal.rest.categories.delete;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository implementation for DELETE category operation.
 * Delegates to the shared CategoryDataAccess for database operations.
 */
@Repository
@RequiredArgsConstructor
public class DeleteCategoryRepository implements DeleteCategoryDataAccess {

    private final CategoryDataAccess categoryDataAccess;

    @Override
    public Optional<Category> findByIdAndUserId(Long id, Long userId) {
        return categoryDataAccess.findByIdAndUserId(id, userId);
    }

    @Override
    public boolean hasTransactions(Long categoryId) {
        return categoryDataAccess.hasTransactions(categoryId);
    }

    @Override
    public void hardDelete(Long id) {
        categoryDataAccess.deleteById(id);
    }

    @Override
    public void softDelete(Long id) {
        categoryDataAccess.archive(id);
    }
}
