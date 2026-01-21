package com.sathira.miimoneypal.rest.transactions.put;

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
 * UseCase for updating transactions.
 * Enforces business rules including category/bucket validation,
 * balance constraints, and prevents modification of system transactions.
 */
@Service
@RequiredArgsConstructor
public class PutTransactionUseCase
        implements AuthenticatedUseCase<PutTransactionRequest, PutTransactionResponse> {

    private final PutTransactionDataAccess transactionDataAccess;
    private final CategoryDataAccess categoryDataAccess;
    private final BucketDataAccess bucketDataAccess;
    private final PutTransactionResponseBuilder responseBuilder;

    @Override
    @Transactional
    public PutTransactionResponse execute(PutTransactionRequest request, AppUser user) {
        // 1. Find existing transaction (enforces user ownership)
        Transaction existingTransaction = transactionDataAccess.findByIdAndUserId(request.id(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));

        // 2. Prevent updating system-generated transactions
        if (existingTransaction.type() == TransactionType.GOAL_COMPLETED) {
            throw new BadRequestException("System-generated transactions cannot be modified");
        }

        // 3. Validate category/bucket based on existing transaction type
        Category category = null;
        Bucket bucket = null;

        switch (existingTransaction.type()) {
            case INCOME, EXPENSE -> {
                if (request.categoryId() == null) {
                    throw new BadRequestException(
                            existingTransaction.type() + " transactions require a category_id"
                    );
                }
                if (request.bucketId() != null) {
                    throw new BadRequestException(
                            existingTransaction.type() + " transactions cannot have a bucket_id"
                    );
                }
                category = validateCategory(request.categoryId(), existingTransaction.type(), user.getId());
            }
            case INVESTMENT, WITHDRAWAL -> {
                if (request.bucketId() == null) {
                    throw new BadRequestException(
                            existingTransaction.type() + " transactions require a bucket_id"
                    );
                }
                if (request.categoryId() != null) {
                    throw new BadRequestException(
                            existingTransaction.type() + " transactions cannot have a category_id"
                    );
                }
                // For withdrawals, validate balance considering the amount change
                bucket = validateBucket(
                        request.bucketId(),
                        existingTransaction.type(),
                        request.amount(),
                        existingTransaction.amount(),
                        existingTransaction.id(),
                        user.getId()
                );
            }
        }

        // 4. Build updated transaction domain record
        Transaction updatedTransaction = Transaction.builder()
                .id(existingTransaction.id())
                .userId(existingTransaction.userId())
                .type(existingTransaction.type())  // Type cannot change
                .amount(request.amount())
                .transactionDate(request.transactionDate())
                .categoryId(request.categoryId())
                .bucketId(request.bucketId())
                .note(request.note())
                .createdAt(existingTransaction.createdAt())  // Preserve original creation time
                .build();

        // 5. Persist update
        Transaction savedTransaction = transactionDataAccess.update(updatedTransaction);

        // 6. Build and return response
        return responseBuilder.build(savedTransaction, category, bucket);
    }

    /**
     * Validate category exists, belongs to user, matches transaction type, and is not archived.
     */
    private Category validateCategory(Long categoryId, TransactionType type, Long userId) {
        Category category = categoryDataAccess.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or access denied"));

        if (category.isArchived()) {
            throw new BusinessRuleException("Cannot use archived category: " + category.name());
        }

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
     * For withdrawals, considers the amount delta to prevent overdraft.
     */
    private Bucket validateBucket(
            Long bucketId,
            TransactionType type,
            BigDecimal newAmount,
            BigDecimal oldAmount,
            Long transactionId,
            Long userId
    ) {
        Bucket bucket = bucketDataAccess.findByIdAndUserId(bucketId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket not found or access denied"));

        if (bucket.isArchived()) {
            throw new BusinessRuleException("Cannot use archived bucket: " + bucket.name());
        }

        // For withdrawals, validate balance considering the amount change
        if (type == TransactionType.WITHDRAWAL) {
            BigDecimal currentBalance = bucketDataAccess.calculateBalance(bucketId);

            // Calculate the delta: how much more (or less) we're withdrawing
            // If increasing withdrawal amount, delta is positive (more being withdrawn)
            // If decreasing withdrawal amount, delta is negative (less being withdrawn)
            BigDecimal amountDelta = newAmount.subtract(oldAmount);

            // Current balance already accounts for the old withdrawal
            // So we need to check if we have enough for the additional withdrawal (if delta is positive)
            BigDecimal availableBalance = currentBalance.add(oldAmount).subtract(newAmount);

            if (availableBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessRuleException(
                        String.format("Insufficient balance in bucket '%s'. Available: %s, Requested withdrawal: %s",
                                bucket.name(), currentBalance.add(oldAmount), newAmount)
                );
            }
        }

        return bucket;
    }
}
