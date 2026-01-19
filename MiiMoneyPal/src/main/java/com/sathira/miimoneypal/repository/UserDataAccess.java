package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.records.user.User;

import java.util.Optional;

/**
 * Data access interface for user operations.
 *
 * This is a SHARED data access interface (lives in repository/ package) because it's used by 3+ endpoints:
 * - login (findByEmail)
 * - register (save, existsByEmail)
 * - refresh token (findById)
 * - profile update (findById, updateCurrencySymbol)
 *
 * Implementation: UserRepository uses jOOQ for type-safe SQL operations.
 */
public interface UserDataAccess {

    /**
     * Find user by email address (case-insensitive).
     *
     * @param email the email address to search for
     * @return Optional containing user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by ID.
     *
     * @param id the user ID
     * @return Optional containing user if found, empty otherwise
     */
    Optional<User> findById(Long id);

    /**
     * Save a new user or update existing user.
     *
     * If user.id() is null, performs INSERT and returns user with generated ID.
     * If user.id() exists, performs UPDATE.
     *
     * @param user the user to save
     * @return saved user with ID populated
     * @throws com.sathira.miimoneypal.exception.DuplicateResourceException if email already exists (on INSERT)
     */
    User save(User user);

    /**
     * Check if user with given email exists (case-insensitive).
     *
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Update user's currency symbol.
     *
     * @param userId the user ID
     * @param currencySymbol the new currency symbol (e.g., "USD", "LKR", "EUR")
     * @throws com.sathira.miimoneypal.exception.ResourceNotFoundException if user not found
     */
    void updateCurrencySymbol(Long userId, String currencySymbol);
}
