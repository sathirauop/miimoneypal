-- MiiMoneyPal Database Schema
-- V1: Core tables with constraints and indexes
-- Flyway migration for PostgreSQL

-- ============================================
-- Table: users
-- Purpose: Store user authentication and preferences
-- ============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    currency_symbol VARCHAR(10) DEFAULT 'LKR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for fast login lookups
CREATE INDEX idx_users_email ON users(email);

-- ============================================
-- Table: categories
-- Purpose: Categorize income and expense transactions
-- Design: UNIQUE(user_id, name, type) allows same name for different types
--         (e.g., "Other" for both INCOME and EXPENSE)
-- ============================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    is_system BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(user_id, name, type)
);

-- Index for user's category list
CREATE INDEX idx_categories_user_id ON categories(user_id);
-- Index for filtering active categories
CREATE INDEX idx_categories_archived ON categories(user_id, is_archived);

-- ============================================
-- Table: buckets
-- Purpose: Track savings goals and perpetual assets (investments)
-- Design: CHECK constraint ensures only SAVINGS_GOAL can have target_amount
-- ============================================
CREATE TABLE buckets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('SAVINGS_GOAL', 'PERPETUAL_ASSET')),
    target_amount DECIMAL(15,2),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'ARCHIVED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(user_id, name),
    CHECK (type = 'SAVINGS_GOAL' OR target_amount IS NULL)
);

-- Index for user's bucket list
CREATE INDEX idx_buckets_user_id ON buckets(user_id);
-- Index for filtering active buckets
CREATE INDEX idx_buckets_status ON buckets(user_id, status);

-- ============================================
-- Table: transactions
-- Purpose: Record all financial movements
-- Design:
--   - ON DELETE RESTRICT for category/bucket prevents accidental data loss
--   - CHECK constraint enforces category/bucket mutual exclusivity:
--     * INCOME/EXPENSE require category_id (no bucket_id)
--     * INVESTMENT/WITHDRAWAL/GOAL_COMPLETED require bucket_id (no category_id)
--   - Amount must always be positive (> 0)
-- ============================================
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE', 'INVESTMENT', 'WITHDRAWAL', 'GOAL_COMPLETED')),
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    transaction_date DATE NOT NULL,
    category_id BIGINT REFERENCES categories(id) ON DELETE RESTRICT,
    bucket_id BIGINT REFERENCES buckets(id) ON DELETE RESTRICT,
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Mutual exclusivity constraint:
    -- INCOME/EXPENSE must have category, no bucket
    -- INVESTMENT/WITHDRAWAL/GOAL_COMPLETED must have bucket, no category
    CHECK (
        (type IN ('INCOME', 'EXPENSE') AND category_id IS NOT NULL AND bucket_id IS NULL)
        OR
        (type IN ('INVESTMENT', 'WITHDRAWAL', 'GOAL_COMPLETED') AND bucket_id IS NOT NULL AND category_id IS NULL)
    )
);

-- Index for recent transactions query (user_id + date DESC)
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date DESC);
-- Index for category-based queries and deletion checks
CREATE INDEX idx_transactions_category ON transactions(category_id);
-- Index for bucket balance calculations
CREATE INDEX idx_transactions_bucket ON transactions(bucket_id);
-- Index for fast monthly queries (dashboard, reports)
CREATE INDEX idx_transactions_month ON transactions(user_id, EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date));
