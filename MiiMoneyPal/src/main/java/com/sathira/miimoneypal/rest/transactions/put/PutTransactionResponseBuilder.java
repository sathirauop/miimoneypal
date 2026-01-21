package com.sathira.miimoneypal.rest.transactions.put;

import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;

/**
 * Interface for building PUT transaction response.
 * Transforms domain Transaction and related entities into response DTO.
 */
public interface PutTransactionResponseBuilder {

    /**
     * Build response from updated transaction and related entities.
     *
     * @param transaction the updated transaction domain record
     * @param category    the related category (null for bucket transactions)
     * @param bucket      the related bucket (null for categorized transactions)
     * @return response DTO with formatted data
     */
    PutTransactionResponse build(Transaction transaction, Category category, Bucket bucket);
}
