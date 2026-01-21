package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.jooq.tables.records.BucketsRecord;
import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.bucket.BucketStatus;
import com.sathira.miimoneypal.records.bucket.BucketType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Buckets.BUCKETS;
import static com.sathira.miimoneypal.jooq.tables.Transactions.TRANSACTIONS;

/**
 * jOOQ implementation of BucketDataAccess.
 * Provides type-safe database queries for bucket operations.
 */
@Repository
@RequiredArgsConstructor
public class BucketRepository implements BucketDataAccess {

    private final DSLContext dsl;

    @Override
    public Optional<Bucket> findById(Long id) {
        return dsl.selectFrom(BUCKETS)
                .where(BUCKETS.ID.eq(id))
                .fetchOptional()
                .map(this::toDomainRecord);
    }

    @Override
    public Optional<Bucket> findByIdAndUserId(Long id, Long userId) {
        return dsl.selectFrom(BUCKETS)
                .where(BUCKETS.ID.eq(id))
                .and(BUCKETS.USER_ID.eq(userId))
                .fetchOptional()
                .map(this::toDomainRecord);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return dsl.fetchExists(
                dsl.selectFrom(BUCKETS)
                        .where(BUCKETS.ID.eq(id))
                        .and(BUCKETS.USER_ID.eq(userId))
        );
    }

    @Override
    public BigDecimal calculateBalance(Long bucketId) {
        /*
         * Calculate bucket balance by summing transactions:
         * - INVESTMENT: increases balance (+amount)
         * - WITHDRAWAL: decreases balance (-amount)
         * - GOAL_COMPLETED: decreases balance (-amount, but system-generated when marking as spent)
         *
         * SQL: SELECT COALESCE(SUM(
         *          CASE
         *              WHEN type = 'INVESTMENT' THEN amount
         *              WHEN type IN ('WITHDRAWAL', 'GOAL_COMPLETED') THEN -amount
         *          END
         *      ), 0) FROM transactions WHERE bucket_id = ?
         */
        BigDecimal balance = dsl.select(
                org.jooq.impl.DSL.coalesce(
                        org.jooq.impl.DSL.sum(
                                org.jooq.impl.DSL.case_()
                                        .when(TRANSACTIONS.TYPE.eq("INVESTMENT"), TRANSACTIONS.AMOUNT)
                                        .when(TRANSACTIONS.TYPE.in("WITHDRAWAL", "GOAL_COMPLETED"),
                                                TRANSACTIONS.AMOUNT.neg())
                                        .otherwise(BigDecimal.ZERO)
                        ),
                        BigDecimal.ZERO
                )
        )
        .from(TRANSACTIONS)
        .where(TRANSACTIONS.BUCKET_ID.eq(bucketId))
        .fetchOne(0, BigDecimal.class);

        return balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * Convert jOOQ BucketsRecord to domain Bucket record.
     */
    private Bucket toDomainRecord(BucketsRecord record) {
        return Bucket.builder()
                .id(record.getId())
                .userId(record.getUserId())
                .name(record.getName())
                .type(BucketType.valueOf(record.getType()))
                .targetAmount(record.getTargetAmount())
                .status(BucketStatus.valueOf(record.getStatus()))
                .createdAt(record.getCreatedAt())
                .build();
    }
}
