package com.sathira.miimoneypal.rest.transactions.delete;

import com.sathira.miimoneypal.jooq.tables.records.TransactionsRecord;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Transactions.TRANSACTIONS;

/**
 * jOOQ implementation for deleting transactions.
 * Handles user-scoped deletion with hard delete (permanent removal).
 */
@Repository
@RequiredArgsConstructor
public class DeleteTransactionRepository implements DeleteTransactionDataAccess {

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
    public boolean deleteByIdAndUserId(Long id, Long userId) {
        int deletedRows = dsl.deleteFrom(TRANSACTIONS)
                .where(TRANSACTIONS.ID.eq(id))
                .and(TRANSACTIONS.USER_ID.eq(userId))
                .execute();

        return deletedRows > 0;
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
