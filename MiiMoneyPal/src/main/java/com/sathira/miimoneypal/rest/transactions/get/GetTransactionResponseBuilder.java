package com.sathira.miimoneypal.rest.transactions.get;

import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;

/**
 * Interface for building GET transaction response.
 * Transforms domain Transaction and related entities into response DTO.
 */
public interface GetTransactionResponseBuilder {

    /**
     * Build response from transaction and related entities.
     *
     * @param transaction the transaction domain record
     * @param category    the related category (null for bucket transactions)
     * @param bucket      the related bucket (null for categorized transactions)
     * @return response DTO with formatted data and related entity details
     */
    GetTransactionResponse build(Transaction transaction, Category category, Bucket bucket);
}
