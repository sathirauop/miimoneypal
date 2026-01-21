package com.sathira.miimoneypal.rest.transactions.list;

import com.sathira.miimoneypal.records.transaction.Transaction;

import java.util.List;

/**
 * Interface for building LIST transactions response.
 * Transforms domain Transaction list into paginated response with related entity names.
 */
public interface ListTransactionsResponseBuilder {

    /**
     * Build paginated response from transactions and pagination metadata.
     * Fetches related category/bucket names for all transactions efficiently.
     *
     * @param transactions list of transaction domain records
     * @param offset       pagination offset
     * @param limit        pagination limit
     * @param totalItems   total number of items matching filters
     * @return paginated response with transaction summaries
     */
    ListTransactionsResponse build(
            List<Transaction> transactions,
            int offset,
            int limit,
            long totalItems
    );
}
