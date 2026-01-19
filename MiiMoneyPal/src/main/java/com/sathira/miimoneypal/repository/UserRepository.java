package com.sathira.miimoneypal.repository;

import com.sathira.miimoneypal.exception.DuplicateResourceException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.jooq.tables.records.UsersRecord;
import com.sathira.miimoneypal.records.user.User;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.sathira.miimoneypal.jooq.tables.Users.USERS;

/**
 * jOOQ-based implementation of UserDataAccess.
 *
 * Uses DSLContext for type-safe SQL operations.
 * Converts between jOOQ UsersRecord and domain User record.
 */
@Repository
@RequiredArgsConstructor
public class UserRepository implements UserDataAccess {

    private final DSLContext dsl;

    @Override
    public Optional<User> findByEmail(String email) {
        return dsl.selectFrom(USERS)
                .where(USERS.EMAIL.equalIgnoreCase(email))
                .fetchOptional()
                .map(UserRepository::toDomainRecord);
    }

    @Override
    public Optional<User> findById(Long id) {
        return dsl.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional()
                .map(UserRepository::toDomainRecord);
    }

    @Override
    public User save(User user) {
        if (user.id() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(USERS)
                        .where(USERS.EMAIL.equalIgnoreCase(email))
        );
    }

    @Override
    public void updateCurrencySymbol(Long userId, String currencySymbol) {
        int rowsUpdated = dsl.update(USERS)
                .set(USERS.CURRENCY_SYMBOL, currencySymbol)
                .set(USERS.UPDATED_AT, LocalDateTime.now())
                .where(USERS.ID.eq(userId))
                .execute();

        if (rowsUpdated == 0) {
            throw new ResourceNotFoundException("User", userId);
        }
    }

    // ========== Private Helper Methods ==========

    /**
     * Insert new user and return with generated ID.
     *
     * @param user the user to insert (id must be null)
     * @return inserted user with generated ID and timestamps
     * @throws DuplicateResourceException if email already exists
     * @throws IllegalArgumentException if passwordHash is null or blank
     */
    private User insert(User user) {
        // Validate required fields for new user registration
        if (user.passwordHash() == null || user.passwordHash().isBlank()) {
            throw new IllegalArgumentException("passwordHash is required for new users");
        }

        try {
            LocalDateTime now = LocalDateTime.now();

            UsersRecord record = dsl.insertInto(USERS)
                    .set(USERS.EMAIL, user.email())
                    .set(USERS.PASSWORD_HASH, user.passwordHash())
                    .set(USERS.CURRENCY_SYMBOL, user.currencySymbol())
                    .set(USERS.CREATED_AT, now)
                    .set(USERS.UPDATED_AT, now)
                    .returning()
                    .fetchOne();

            if (record == null) {
                throw new IllegalStateException("Failed to insert user - no record returned");
            }

            return toDomainRecord(record);

        } catch (DataAccessException e) {
            // Check for unique constraint violation using SQLState code (23505 = unique_violation)
            // This is more robust than checking error message text
            if (isDuplicateKeyViolation(e)) {
                throw new DuplicateResourceException("User", "email", user.email());
            }
            throw e; // Re-throw unexpected database errors
        }
    }

    /**
     * Check if the exception is a duplicate key (unique constraint) violation.
     * Uses SQLState code 23505 which is JDBC/PostgreSQL standard for unique violations.
     */
    private boolean isDuplicateKeyViolation(DataAccessException e) {
        // Check for Spring's DuplicateKeyException wrapper
        if (e.getCause() instanceof DuplicateKeyException) {
            return true;
        }
        // Fallback: Check error message for constraint name (PostgreSQL specific)
        return e.getMessage() != null && e.getMessage().contains("users_email_key");
    }

    /**
     * Update existing user.
     *
     * @param user the user to update (id must not be null)
     * @return updated user with refreshed timestamps
     * @throws ResourceNotFoundException if user not found
     */
    private User update(User user) {
        UsersRecord record = dsl.update(USERS)
                .set(USERS.EMAIL, user.email())
                .set(USERS.PASSWORD_HASH, user.passwordHash())
                .set(USERS.CURRENCY_SYMBOL, user.currencySymbol())
                .set(USERS.UPDATED_AT, LocalDateTime.now())
                .where(USERS.ID.eq(user.id()))
                .returning()
                .fetchOne();

        if (record == null) {
            throw new ResourceNotFoundException("User", user.id());
        }

        return toDomainRecord(record);
    }

    /**
     * Convert jOOQ UsersRecord to immutable domain User record.
     *
     * @param record the jOOQ record from database
     * @return domain User record with all fields populated
     */
    private static User toDomainRecord(UsersRecord record) {
        return User.builder()
                .id(record.getId())
                .email(record.getEmail())
                .passwordHash(record.getPasswordHash())
                .currencySymbol(record.getCurrencySymbol())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
