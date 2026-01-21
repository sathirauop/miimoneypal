package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;

/**
 * Interface for building transaction creation response.
 * Transforms domain Transaction and related entities into response DTO.
 */
public interface PostTransactionResponseBuilder {

    /**
     * Build response from transaction and related entities.
     *
     * @param transaction the created transaction
     * @param category    the related category (null for bucket transactions)
     * @param bucket      the related bucket (null for categorized transactions)
     * @return response DTO with formatted data
     */
    PostTransactionResponse build(Transaction transaction, Category category, Bucket bucket);
}
