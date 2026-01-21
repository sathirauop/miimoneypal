package com.sathira.miimoneypal.rest.transactions.delete;

import com.sathira.miimoneypal.records.transaction.Transaction;

import java.util.Optional;

/**
 * Data access contract for deleting transactions.
 * Enforces user-scoped queries for security.
 */
public interface DeleteTransactionDataAccess {

    /**
     * Find a transaction by ID that belongs to the specified user.
     * Used to verify ownership before deletion.
     *
     * @param id     transaction ID
     * @param userId owner user ID
     * @return Optional containing the transaction if found and belongs to user, empty otherwise
     */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    /**
     * Delete a transaction by ID.
     * Only deletes if the transaction belongs to the specified user.
     *
     * @param id     transaction ID
     * @param userId owner user ID
     * @return true if transaction was deleted, false if not found or doesn't belong to user
     */
    boolean deleteByIdAndUserId(Long id, Long userId);
}
