package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UseCase for listing transactions with filters and pagination.
 * Orchestrates data fetching, counting, and response building.
 */
@Service
@RequiredArgsConstructor
public class ListTransactionsUseCase
        implements AuthenticatedUseCase<ListTransactionsRequest, ListTransactionsResponse> {

    private final ListTransactionsDataAccess transactionDataAccess;
    private final ListTransactionsResponseBuilder responseBuilder;

    @Override
    @Transactional(readOnly = true)
    public ListTransactionsResponse execute(ListTransactionsRequest request, AppUser user) {
        // 1. Fetch transactions matching filters with pagination
        List<Transaction> transactions = transactionDataAccess.findByFilters(
                user.getId(),
                request.type(),
                request.startDate(),
                request.endDate(),
                request.categoryId(),
                request.bucketId(),
                request.searchTerm(),
                request.offset(),
                request.limit()
        );

        // 2. Count total items for pagination metadata
        long totalItems = transactionDataAccess.countByFilters(
                user.getId(),
                request.type(),
                request.startDate(),
                request.endDate(),
                request.categoryId(),
                request.bucketId(),
                request.searchTerm()
        );

        // 3. Build and return paginated response
        return responseBuilder.build(transactions, request.offset(), request.limit(), totalItems);
    }
}
