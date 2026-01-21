package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.records.transaction.Transaction;

/**
 * Data access contract for creating transactions.
 */
public interface PostTransactionDataAccess {

    /**
     * Persist a new transaction to the database.
     *
     * @param transaction the transaction domain record to create
     * @return the persisted transaction with generated ID and timestamps
     */
    Transaction create(Transaction transaction);
}
