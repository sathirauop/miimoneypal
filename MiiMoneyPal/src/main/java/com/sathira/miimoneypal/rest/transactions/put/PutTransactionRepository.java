package com.sathira.miimoneypal.rest.transactions.put;

import com.sathira.miimoneypal.jooq.tables.records.TransactionsRecord;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Transactions.TRANSACTIONS;

/**
 * jOOQ implementation for updating transactions.
 * Handles user-scoped queries and updates with optimistic locking via updated_at.
 */
@Repository
@RequiredArgsConstructor
public class PutTransactionRepository implements PutTransactionDataAccess {

    private final DSLContext dsl;

    @Override
    public Optional<Transaction> findByIdAndUserId(Long id, Long userId) {
        return dsl.selectFrom(TRANSACTIONS)
                .where(TRANSACTIONS.ID.eq(id))
                .and(TRANSACTIONS.USER_ID.eq(userId))
                .fetchOptional()
                .map(this::toDomainRecord);
    }

    @Override
    public Transaction update(Transaction transaction) {
        // Update the transaction record
        int updatedRows = dsl.update(TRANSACTIONS)
                .set(TRANSACTIONS.AMOUNT, transaction.amount())
                .set(TRANSACTIONS.TRANSACTION_DATE, transaction.transactionDate())
                .set(TRANSACTIONS.CATEGORY_ID, transaction.categoryId())
                .set(TRANSACTIONS.BUCKET_ID, transaction.bucketId())
                .set(TRANSACTIONS.NOTE, transaction.note())
                .set(TRANSACTIONS.UPDATED_AT, java.time.LocalDateTime.now())
                .where(TRANSACTIONS.ID.eq(transaction.id()))
                .and(TRANSACTIONS.USER_ID.eq(transaction.userId()))
                .execute();

        if (updatedRows == 0) {
            throw new IllegalStateException("Transaction update failed - no rows affected");
        }

        // Fetch and return the updated record
        return dsl.selectFrom(TRANSACTIONS)
                .where(TRANSACTIONS.ID.eq(transaction.id()))
                .fetchOne(this::toDomainRecord);
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
