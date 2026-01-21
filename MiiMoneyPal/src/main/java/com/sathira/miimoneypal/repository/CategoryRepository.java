package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.jooq.tables.records.CategoriesRecord;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Categories.CATEGORIES;
import static com.sathira.miimoneypal.jooq.tables.Transactions.TRANSACTIONS;

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

    @Override
    public boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type) {
        return dsl.fetchExists(
                dsl.selectFrom(CATEGORIES)
                        .where(CATEGORIES.USER_ID.eq(userId))
                        .and(CATEGORIES.NAME.eq(name))
                        .and(CATEGORIES.TYPE.eq(type.name()))
        );
    }

    @Override
    public boolean hasTransactions(Long categoryId) {
        return dsl.fetchExists(
                dsl.selectFrom(TRANSACTIONS)
                        .where(TRANSACTIONS.CATEGORY_ID.eq(categoryId))
        );
    }

    @Override
    public List<Category> findAllByUserId(Long userId, CategoryType type, boolean includeArchived) {
        var query = dsl.selectFrom(CATEGORIES)
                .where(CATEGORIES.USER_ID.eq(userId));

        // Apply type filter if provided
        if (type != null) {
            query = query.and(CATEGORIES.TYPE.eq(type.name()));
        }

        // Apply archived filter
        if (!includeArchived) {
            query = query.and(CATEGORIES.IS_ARCHIVED.eq(false));
        }

        return query.orderBy(CATEGORIES.NAME.asc())
                .fetch()
                .map(this::toDomainRecord);
    }

    @Override
    public Category save(Category category) {
        CategoriesRecord record = dsl.newRecord(CATEGORIES);
        record.setUserId(category.userId());
        record.setName(category.name());
        record.setType(category.type().name());
        record.setColor(category.color());
        record.setIcon(category.icon());
        record.setIsSystem(category.isSystem());
        record.setIsArchived(category.isArchived());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        record.store();
        return toDomainRecord(record);
    }

    @Override
    public Category update(Category category) {
        dsl.update(CATEGORIES)
                .set(CATEGORIES.NAME, category.name())
                .set(CATEGORIES.COLOR, category.color())
                .set(CATEGORIES.ICON, category.icon())
                .set(CATEGORIES.UPDATED_AT, LocalDateTime.now())
                .where(CATEGORIES.ID.eq(category.id()))
                .execute();

        return findById(category.id()).orElseThrow();
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(CATEGORIES)
                .where(CATEGORIES.ID.eq(id))
                .execute();
    }

    @Override
    public void archive(Long id) {
        dsl.update(CATEGORIES)
                .set(CATEGORIES.IS_ARCHIVED, true)
                .set(CATEGORIES.UPDATED_AT, LocalDateTime.now())
                .where(CATEGORIES.ID.eq(id))
                .execute();
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
                .color(record.getColor())
                .icon(record.getIcon())
                .isSystem(record.getIsSystem())
                .isArchived(record.getIsArchived())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
