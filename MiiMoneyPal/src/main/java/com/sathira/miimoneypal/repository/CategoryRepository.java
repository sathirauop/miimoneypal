package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.jooq.tables.records.CategoriesRecord;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Categories.CATEGORIES;

/**
 * jOOQ implementation of CategoryDataAccess.
 * Provides type-safe database queries for category operations.
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepository implements CategoryDataAccess {

    private final DSLContext dsl;

    @Override
    public Optional<Category> findById(Long id) {
        return dsl.selectFrom(CATEGORIES)
                .where(CATEGORIES.ID.eq(id))
                .fetchOptional()
                .map(this::toDomainRecord);
    }

    @Override
    public Optional<Category> findByIdAndUserId(Long id, Long userId) {
        return dsl.selectFrom(CATEGORIES)
                .where(CATEGORIES.ID.eq(id))
                .and(CATEGORIES.USER_ID.eq(userId))
                .fetchOptional()
                .map(this::toDomainRecord);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return dsl.fetchExists(
                dsl.selectFrom(CATEGORIES)
                        .where(CATEGORIES.ID.eq(id))
                        .and(CATEGORIES.USER_ID.eq(userId))
        );
    }

    /**
     * Convert jOOQ CategoriesRecord to domain Category record.
     */
    private Category toDomainRecord(CategoriesRecord record) {
        return Category.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .name(record.getName())
                .type(CategoryType.valueOf(record.getType()))
                .isSystem(record.getIsSystem())
                .isArchived(record.getIsArchived())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
