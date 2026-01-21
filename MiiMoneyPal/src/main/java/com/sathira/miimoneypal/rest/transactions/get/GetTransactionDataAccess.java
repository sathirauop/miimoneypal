package com.sathira.miimoneypal.rest.transactions.get;

import com.sathira.miimoneypal.records.transaction.Transaction;

import java.util.Optional;

/**
 * Data access contract for fetching a single transaction.
 * Enforces user-scoped queries for security.
 */
public interface GetTransactionDataAccess {

    /**
     * Find a transaction by ID that belongs to the specified user.
     *
     * @param id     transaction ID
     * @param userId owner user ID
     * @return Optional containing the transaction if found and belongs to user, empty otherwise
     */
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
}
