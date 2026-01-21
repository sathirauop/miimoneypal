package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.jooq.tables.records.TransactionsRecord;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.sathira.miimoneypal.jooq.tables.Transactions.TRANSACTIONS;

/**
 * jOOQ implementation for listing transactions with dynamic filters.
 * Builds WHERE clause dynamically based on provided filter parameters.
 */
@Repository
@RequiredArgsConstructor
public class ListTransactionsRepository implements ListTransactionsDataAccess {

    private final DSLContext dsl;

    @Override
    public List<Transaction> findByFilters(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long bucketId,
            String searchTerm,
            int offset,
            int limit
    ) {
        Condition condition = buildWhereCondition(userId, type, startDate, endDate,
                categoryId, bucketId, searchTerm);

        return dsl.selectFrom(TRANSACTIONS)
                .where(condition)
                .orderBy(TRANSACTIONS.TRANSACTION_DATE.desc(), TRANSACTIONS.CREATED_AT.desc())
                .limit(limit)
                .offset(offset)
                .fetch()
                .map(this::toDomainRecord);
    }

    @Override
    public long countByFilters(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long bucketId,
            String searchTerm
    ) {
        Condition condition = buildWhereCondition(userId, type, startDate, endDate,
                categoryId, bucketId, searchTerm);

        Long count = dsl.selectCount()
                .from(TRANSACTIONS)
                .where(condition)
                .fetchOne(0, Long.class);

        return count != null ? count : 0L;
    }

    /**
     * Build dynamic WHERE condition based on provided filters.
     * Always includes user_id filter for security.
     */
    private Condition buildWhereCondition(
            Long userId,
            TransactionType type,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId,
            Long bucketId,
            String searchTerm
    ) {
        List<Condition> conditions = new ArrayList<>();

        // Always filter by user_id for security
        conditions.add(TRANSACTIONS.USER_ID.eq(userId));

        // Optional filters
        if (type != null) {
            conditions.add(TRANSACTIONS.TYPE.eq(type.name()));
        }

        if (startDate != null) {
            conditions.add(TRANSACTIONS.TRANSACTION_DATE.greaterOrEqual(startDate));
        }

        if (endDate != null) {
            conditions.add(TRANSACTIONS.TRANSACTION_DATE.lessOrEqual(endDate));
        }

        if (categoryId != null) {
            conditions.add(TRANSACTIONS.CATEGORY_ID.eq(categoryId));
        }

        if (bucketId != null) {
            conditions.add(TRANSACTIONS.BUCKET_ID.eq(bucketId));
        }

        if (searchTerm != null && !searchTerm.isBlank()) {
            // Case-insensitive search in notes using ILIKE (PostgreSQL)
            conditions.add(TRANSACTIONS.NOTE.likeIgnoreCase("%" + searchTerm + "%"));
        }

        // Combine all conditions with AND
        return org.jooq.impl.DSL.and(conditions);
    }

    /**
     * Convert jOOQ TransactionsRecord to domain Transaction record.
     */
    private Transaction toDomainRecord(TransactionsRecord record) {
        return Transaction.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .type(TransactionType.valueOf(record.getType()))
                .amount(record.getAmount())
                .transactionDate(record.getTransactionDate())
                .categoryId(record.getCategoryId())
                .bucketId(record.getBucketId())
                .note(record.getNote())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
