-- MiiMoneyPal Database Schema
-- V2: Views for computed data
-- Flyway migration for PostgreSQL

-- ============================================
-- View: bucket_balances
-- Purpose: Provide pre-calculated bucket balances for faster queries
-- Formula: Σ(INVESTMENT) - Σ(WITHDRAWAL) - Σ(GOAL_COMPLETED)
--
-- Performance Note: For V2, consider materializing this view
-- and refreshing on transaction changes for high-volume users.
-- ============================================
CREATE VIEW bucket_balances AS
SELECT
    b.id AS bucket_id,
    b.user_id,
    b.name,
    b.type,
    b.target_amount,
    b.status,
    COALESCE(
        SUM(
            CASE
                WHEN t.type = 'INVESTMENT' THEN t.amount
                WHEN t.type = 'WITHDRAWAL' THEN -t.amount
                WHEN t.type = 'GOAL_COMPLETED' THEN -t.amount
                ELSE 0
            END
        ), 0
    ) AS current_balance
FROM buckets b
LEFT JOIN transactions t ON b.id = t.bucket_id
GROUP BY b.id, b.user_id, b.name, b.type, b.target_amount, b.status;

-- ============================================
-- View: monthly_summary
-- Purpose: Provide pre-calculated monthly totals for dashboard
-- Includes: income, expenses, investments, withdrawals per user per month
-- ============================================
CREATE VIEW monthly_summary AS
SELECT
    user_id,
    EXTRACT(YEAR FROM transaction_date)::INTEGER AS year,
    EXTRACT(MONTH FROM transaction_date)::INTEGER AS month,
    COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) AS total_income,
    COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) AS total_expenses,
    COALESCE(SUM(CASE WHEN type = 'INVESTMENT' THEN amount ELSE 0 END), 0) AS total_investments,
    COALESCE(SUM(CASE WHEN type = 'WITHDRAWAL' THEN amount ELSE 0 END), 0) AS total_withdrawals,
    COALESCE(SUM(CASE WHEN type = 'GOAL_COMPLETED' THEN amount ELSE 0 END), 0) AS total_goal_completed
FROM transactions
GROUP BY user_id, EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date);
