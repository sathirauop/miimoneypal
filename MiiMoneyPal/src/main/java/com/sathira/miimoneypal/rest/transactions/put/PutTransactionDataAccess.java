package com.sathira.miimoneypal.rest.transactions.put;

import com.sathira.miimoneypal.records.transaction.Transaction;

import java.util.Optional;

/**
 * Data access contract for updating transactions.
 * Enforces user-scoped queries for security.
 */
public interface PutTransactionDataAccess {

    /**
     * Find a transaction by ID that belongs to the specified user.
     * Used to verify ownership and get current state before update.
     *
     * @param id     transaction ID
     * @param userId owner user ID
     * @return Optional containing the transaction if found and belongs to user, empty otherwise
     */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    /**
     * Update an existing transaction.
     * Only updates the provided fields; does not modify type, user_id, or id.
     *
     * @param transaction the transaction domain record with updated values
     * @return the updated transaction with new updated_at timestamp
     */
    Transaction update(Transaction transaction);
}
