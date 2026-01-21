package com.sathira.miimoneypal.rest.transactions.get;

import com.sathira.miimoneypal.jooq.tables.records.TransactionsRecord;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Transactions.TRANSACTIONS;

/**
 * jOOQ implementation for fetching a single transaction.
 * Handles user-scoped queries to prevent unauthorized access.
 */
@Repository
@RequiredArgsConstructor
public class GetTransactionRepository implements GetTransactionDataAccess {

    private final DSLContext dsl;

    @Override
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId) {
        return dsl.selectFrom(TRANSACTIONS)
                .where(TRANSACTIONS.ID.eq(id))
                .and(TRANSACTIONS.USER_ID.eq(userId))
                .fetchOptional()
                .map(this::toDomainRecord);
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
