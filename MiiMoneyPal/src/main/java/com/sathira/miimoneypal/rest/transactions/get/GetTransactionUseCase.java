package com.sathira.miimoneypal.rest.transactions.get;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.repository.BucketDataAccess;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for fetching a single transaction by ID.
 * Enforces user-scoped access and includes related entity details.
 */
@Service
@RequiredArgsConstructor
public class GetTransactionUseCase
        implements AuthenticatedUseCase<GetTransactionRequest, GetTransactionResponse> {

    private final GetTransactionDataAccess transactionDataAccess;
    private final CategoryDataAccess categoryDataAccess;
    private final BucketDataAccess bucketDataAccess;
    private final GetTransactionResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public GetTransactionResponse execute(GetTransactionRequest request, AppUser user) {
        // 1. Find transaction (enforces user ownership)
        Transaction transaction = transactionDataAccess.findByIdAndUserId(request.id(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));

        // 2. Fetch related category if present
        Category category = null;
        if (transaction.categoryId() != null) {
            category = categoryDataAccess.findById(transaction.categoryId()).orElse(null);
        }

        // 3. Fetch related bucket if present
        Bucket bucket = null;
        if (transaction.bucketId() != null) {
            bucket = bucketDataAccess.findById(transaction.bucketId()).orElse(null);
        }

        // 4. Build and return response
        return responseBuilder.build(transaction, category, bucket);
    }
}
