package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.exception.BusinessRuleException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import com.sathira.miimoneypal.repository.BucketDataAccess;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * UseCase for creating new transactions.
 * Enforces business rules including category/bucket validation,
 * type matching, and balance constraints.
 */
@Service
@RequiredArgsConstructor
public class PostTransactionUseCase
        implements AuthenticatedUseCase<PostTransactionRequest, PostTransactionResponse> {

    private final PostTransactionDataAccess transactionDataAccess;
    private final CategoryDataAccess categoryDataAccess;
    private final BucketDataAccess bucketDataAccess;
    private final PostTransactionResponseBuilder responseBuilder;

    @Override
    @Transactional
    public PostTransactionResponse execute(PostTransactionRequest request, AppUser user) {
        // 1. Reject system-generated transaction types
        if (request.type() == TransactionType.GOAL_COMPLETED) {
            throw new BadRequestException("GOAL_COMPLETED transactions cannot be created manually");
        }

        // 2. Validate category/bucket requirements based on transaction type
        Category category = null;
        Bucket bucket = null;

        switch (request.type()) {
            case INCOME, EXPENSE -> {
                if (request.categoryId() == null) {
                    throw new BadRequestException(
                            request.type() + " transactions require a category_id"
                    );
                }
                if (request.bucketId() != null) {
                    throw new BadRequestException(
                            request.type() + " transactions cannot have a bucket_id"
                    );
                }
                category = validateCategory(request.categoryId(), request.type(), user.getId());
            }
            case INVESTMENT, WITHDRAWAL -> {
                if (request.bucketId() == null) {
                    throw new BadRequestException(
                            request.type() + " transactions require a bucket_id"
                    );
                }
                if (request.categoryId() != null) {
                    throw new BadRequestException(
                            request.type() + " transactions cannot have a category_id"
                    );
                }
                bucket = validateBucket(request.bucketId(), request.type(), request.amount(), user.getId());
            }
        }

        // 3. Build transaction domain record
        Transaction transaction = Transaction.builder()
                .userId(user.getId())
                .type(request.type())
                .amount(request.amount())
                .transactionDate(request.transactionDate())
                .categoryId(request.categoryId())
                .bucketId(request.bucketId())
                .note(request.note())
                .build();

        // 4. Persist to database
        Transaction savedTransaction = transactionDataAccess.create(transaction);

        // 5. Build and return response
        return responseBuilder.build(savedTransaction, category, bucket);
    }

    /**
     * Validate category exists, belongs to user, matches transaction type, and is not archived.
     *
     * @param categoryId category to validate
     * @param type       transaction type (INCOME or EXPENSE)
     * @param userId     authenticated user ID
     * @return validated Category
     * @throws ResourceNotFoundException if category not found or doesn't belong to user
     * @throws BusinessRuleException     if category type mismatch or category is archived
     */
    private Category validateCategory(Long categoryId, TransactionType type, Long userId) {
        // Check category exists and belongs to user
        Category category = categoryDataAccess.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or access denied"));

        // Check category is not archived
        if (category.isArchived()) {
            throw new BusinessRuleException("Cannot use archived category: " + category.name());
        }

        // Check category type matches transaction type
        CategoryType expectedCategoryType = (type == TransactionType.INCOME)
                ? CategoryType.INCOME
                : CategoryType.EXPENSE;

        if (category.type() != expectedCategoryType) {
            throw new BusinessRuleException(
                    String.format("Category '%s' is type %s but transaction is type %s",
                            category.name(), category.type(), type)
            );
        }

        return category;
    }

    /**
     * Validate bucket exists, belongs to user, is active, and has sufficient balance for withdrawals.
     *
     * @param bucketId bucket to validate
     * @param type     transaction type (INVESTMENT or WITHDRAWAL)
     * @param amount   transaction amount
     * @param userId   authenticated user ID
     * @return validated Bucket
     * @throws ResourceNotFoundException if bucket not found or doesn't belong to user
     * @throws BusinessRuleException     if bucket is archived or insufficient balance for withdrawal
     */
    private Bucket validateBucket(Long bucketId, TransactionType type, BigDecimal amount, Long userId) {
        // Check bucket exists and belongs to user
        Bucket bucket = bucketDataAccess.findByIdAndUserId(bucketId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket not found or access denied"));

        // Check bucket is not archived
        if (bucket.isArchived()) {
            throw new BusinessRuleException("Cannot use archived bucket: " + bucket.name());
        }

        // For withdrawals, check sufficient balance
        if (type == TransactionType.WITHDRAWAL) {
            BigDecimal currentBalance = bucketDataAccess.calculateBalance(bucketId);

            if (currentBalance.compareTo(amount) < 0) {
                throw new BusinessRuleException(
                        String.format("Insufficient balance in bucket '%s'. Current: %s, Withdrawal: %s",
                                bucket.name(), currentBalance, amount)
                );
            }
        }

        return bucket;
    }
}
