package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.repository.BucketDataAccess;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Presenter for LIST transactions response.
 * Efficiently fetches related entities and transforms to paginated response.
 */
@Component
@RequiredArgsConstructor
public class ListTransactionsPresenter implements ListTransactionsResponseBuilder {

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);

    private final CategoryDataAccess categoryDataAccess;
    private final BucketDataAccess bucketDataAccess;

    @Override
    public ListTransactionsResponse build(
            List<Transaction> transactions,
            int offset,
            int limit,
            long totalItems
    ) {
        // Early return for empty list
        if (transactions.isEmpty()) {
            return ListTransactionsResponse.of(List.of(), offset, limit, totalItems);
        }

        // Extract unique category and bucket IDs
        Set<Long> categoryIds = transactions.stream()
                .map(Transaction::categoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> bucketIds = transactions.stream()
                .map(Transaction::bucketId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Fetch categories and buckets in batches (cached lookups)
        Map<Long, Category> categoryMap = fetchCategories(categoryIds);
        Map<Long, Bucket> bucketMap = fetchBuckets(bucketIds);

        // Transform each transaction to summary
        List<TransactionSummary> summaries = transactions.stream()
                .map(transaction -> toSummary(transaction, categoryMap, bucketMap))
                .toList();

        return ListTransactionsResponse.of(summaries, offset, limit, totalItems);
    }

    /**
     * Fetch categories by IDs and return as map.
     */
    private Map<Long, Category> fetchCategories(Set<Long> categoryIds) {
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Long categoryId : categoryIds) {
            categoryDataAccess.findById(categoryId)
                    .ifPresent(category -> categoryMap.put(categoryId, category));
        }
        return categoryMap;
    }

    /**
     * Fetch buckets by IDs and return as map.
     */
    private Map<Long, Bucket> fetchBuckets(Set<Long> bucketIds) {
        Map<Long, Bucket> bucketMap = new HashMap<>();
        for (Long bucketId : bucketIds) {
            bucketDataAccess.findById(bucketId)
                    .ifPresent(bucket -> bucketMap.put(bucketId, bucket));
        }
        return bucketMap;
    }

    /**
     * Transform Transaction domain record to TransactionSummary DTO.
     */
    private TransactionSummary toSummary(
            Transaction transaction,
            Map<Long, Category> categoryMap,
            Map<Long, Bucket> bucketMap
    ) {
        Category category = transaction.categoryId() != null
                ? categoryMap.get(transaction.categoryId())
                : null;

        Bucket bucket = transaction.bucketId() != null
                ? bucketMap.get(transaction.bucketId())
                : null;

        return new TransactionSummary(
                transaction.id(),
                transaction.type().name(),
                transaction.amount(),
                formatAmount(transaction.amount()),
                transaction.transactionDate(),
                category != null ? category.id() : null,
                category != null ? category.name() : null,
                bucket != null ? bucket.id() : null,
                bucket != null ? bucket.name() : null,
                transaction.note(),
                transaction.createdAt()
        );
    }

    /**
     * Format amount as currency string (e.g., "$1,234.56").
     */
    private String formatAmount(java.math.BigDecimal amount) {
        return CURRENCY_FORMATTER.format(amount);
    }
}
