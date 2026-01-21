package com.sathira.miimoneypal.rest.categories.get;

import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository implementation for GET category operation.
 * Delegates to the shared CategoryDataAccess for database operations.
 */
@Repository
@RequiredArgsConstructor
public class GetCategoryRepository implements GetCategoryDataAccess {

    private final CategoryDataAccess categoryDataAccess;

    @Override
    public Optional<Category> findByIdAndUserId(Long id, Long userId) {
        return categoryDataAccess.findByIdAndUserId(id, userId);
    }
}
