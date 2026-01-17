# Architecture Documentation

## Table of Contents
1. [Architectural Overview](#architectural-overview)
2. [Core Architectural Principles](#core-architectural-principles)
3. [System Architecture](#system-architecture)
4. [Domain Model](#domain-model)
5. [Data Flow](#data-flow)
6. [Security Architecture](#security-architecture)
7. [Database Schema Design](#database-schema-design)
8. [API Design Decisions](#api-design-decisions)
9. [State Management Strategy](#state-management-strategy)
10. [Key Design Patterns](#key-design-patterns)
11. [Architectural Constraints & Trade-offs](#architectural-constraints--trade-offs)

---

## Architectural Overview

### Philosophy
MiiMoneyPal follows a **Modular Monolithic** architecture organized by **business features** rather than technical layers. This approach provides:
- Clear boundaries between features
- Easier reasoning about code location
- Simplified testing and maintenance
- Future microservices extraction path (if needed)

### Core Design Pattern: Endpoint-Per-Package
Each REST endpoint lives in its own isolated package with all required components:
- Input/Output DTOs
- Business logic (UseCase)
- Data access contracts (Interfaces)
- Implementations (Repositories, Presenters)

**Rationale:** This prevents the "God Class" anti-pattern and makes features self-contained. When debugging or enhancing a feature, all related code is in one location.

---

## Core Architectural Principles

### 1. Vertical Slicing (Feature-First)
```
❌ Traditional Layered (Horizontal)     ✅ Feature-First (Vertical)

controllers/                            auth/
  - AuthController                        - login/
  - TransactionController                 - register/
services/                                 - refresh/
  - AuthService                         transactions/
  - TransactionService                    - get/
repositories/                             - post/
  - UserRepository                        - put/
  - TransactionRepository                 - delete/
```

**Rationale:** Feature-based organization aligns with how developers think about business requirements. When implementing "add transaction", all related code is in `transactions/post/`, not scattered across layers.

### 2. Dependency Inversion (Clean Architecture)
```
Controller (HTTP) → UseCase Interface
                         ↓
UseCase Implementation → DataAccess Interface → Repository Implementation
                      → ResponseBuilder Interface → Presenter Implementation
```

**Key Rules:**
- **UseCases depend on interfaces, never concrete classes**
- **Business logic never depends on implementation details**
- Allows easy testing (mock interfaces)
- Enables changing implementations without touching business logic

### 3. Single Responsibility Per Class
- **Controller:** HTTP routing only
- **UseCase:** Business logic orchestration
- **Repository:** Data persistence only
- **Presenter:** DTO transformation only

**Rationale:** Each class has one reason to change, making the codebase more maintainable.

### 4. Type Safety First
- Java Records for immutability
- jOOQ for type-safe SQL (compile-time validation)
- No magic strings or reflection-based queries
- Enums for constrained values

**Rationale:** Catch errors at compile time, not runtime. IDE autocomplete and refactoring support.

---

## System Architecture

### High-Level Component Diagram
```
┌─────────────────────────────────────────────────────────────┐
│                     Frontend (React + Vite)                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Dashboard   │  │ Transactions │  │   Buckets    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │             │
│         └──────────────────┴──────────────────┘             │
│                            │                                │
│                   TanStack Query (Cache)                    │
│                            │                                │
│                       Axios + JWT                           │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTPS
                             │
┌────────────────────────────┴────────────────────────────────┐
│                  Backend (Spring Boot 4.0.1 + Java 25)      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Spring Security + JWT Filter            │   │
│  └──────────────────────────────────────────────────────┘   │
│         │                   │                   │            │
│  ┌──────▼─────┐      ┌──────▼──────┐    ┌──────▼─────┐     │
│  │   Auth     │      │Transactions │    │  Buckets   │     │
│  │ Controller │      │ Controller  │    │ Controller │     │
│  └──────┬─────┘      └──────┬──────┘    └──────┬─────┘     │
│         │                   │                   │            │
│  ┌──────▼─────┐      ┌──────▼──────┐    ┌──────▼─────┐     │
│  │   Login    │      │   Post      │    │MarkSpent   │     │
│  │  UseCase   │      │Transaction  │    │  UseCase   │     │
│  │            │      │  UseCase    │    │            │     │
│  └──────┬─────┘      └──────┬──────┘    └──────┬─────┘     │
│         │                   │                   │            │
│  ┌──────▼──────────────────▼───────────────────▼──────┐     │
│  │           jOOQ DSLContext (Type-Safe SQL)          │     │
│  └──────┬──────────────────────────────────────────────┘     │
│         │                                                    │
└─────────┼────────────────────────────────────────────────────┘
          │
┌─────────▼─────────────────────────────────────────────────┐
│                  PostgreSQL 16                            │
│  ┌─────────┐  ┌──────────────┐  ┌────────────┐           │
│  │  users  │  │ transactions │  │  buckets   │           │
│  └─────────┘  └──────────────┘  └────────────┘           │
│  ┌─────────┐  ┌──────────────┐                           │
│  │categories│  │bucket_balances (view)                    │
│  └─────────┘  └──────────────┘                           │
└───────────────────────────────────────────────────────────┘
```

### Request Flow (Example: Create Transaction)
```
1. User submits form → Frontend validates with Zod
2. React Hook Form → Axios POST /api/v1/transactions
3. Spring Security JWT Filter → validates token, extracts user_id
4. TransactionController.createTransaction()
   → validates @RequestBody
   → delegates to PostTransactionUseCase.execute()
5. PostTransactionUseCase
   → validates business rules (category type matches transaction type)
   → calls repository.create() via DataAccess interface
6. PostTransactionRepository
   → uses jOOQ DSL to INSERT INTO transactions
   → returns domain Record (Transaction)
7. PostTransactionPresenter
   → transforms domain Record → Response DTO
8. Controller returns ResponseEntity<PostTransactionResponse>
9. TanStack Query → invalidates cache → refetches dashboard data
10. UI updates with new transaction
```

---

## Domain Model

### Core Entities

#### Transaction (Aggregate Root)
```
Transaction
├── id (PK)
├── user_id (FK) ← Owner isolation
├── type (ENUM: INCOME, EXPENSE, INVESTMENT, WITHDRAWAL, GOAL_COMPLETED)
├── amount (DECIMAL)
├── transaction_date (DATE)
├── category_id (FK, optional) ← Required for INCOME/EXPENSE
├── bucket_id (FK, optional) ← Required for INVESTMENT/WITHDRAWAL
├── note (VARCHAR(255), optional)
└── created_at (TIMESTAMP)

Business Rules:
- amount must be > 0
- category_id required for INCOME/EXPENSE types
- bucket_id required for INVESTMENT/WITHDRAWAL types
- Category type must match transaction type (INCOME category for INCOME transaction)
- Only ONE "Opening Balance" transaction per month per user
- GOAL_COMPLETED transactions auto-generated, not user-created
```

#### Category
```
Category
├── id (PK)
├── user_id (FK)
├── name (VARCHAR)
├── type (ENUM: INCOME, EXPENSE)
├── is_system (BOOLEAN) ← Protected categories (e.g., "Opening Balance")
├── is_archived (BOOLEAN)
└── created_at

Business Rules:
- Cannot delete if transactions exist (must archive or merge)
- System categories cannot be deleted, archived, or renamed
- Each user gets seeded categories on registration
```

#### Bucket
```
Bucket
├── id (PK)
├── user_id (FK)
├── name (VARCHAR)
├── type (ENUM: SAVINGS_GOAL, PERPETUAL_ASSET)
├── target_amount (DECIMAL, optional) ← Only for SAVINGS_GOAL
├── status (ENUM: ACTIVE, ARCHIVED)
└── created_at

Computed Field (via query):
└── current_balance = SUM(investments) - SUM(withdrawals) - SUM(goal_completed)

Business Rules:
- SAVINGS_GOAL can have target_amount, can be "marked as spent"
- PERPETUAL_ASSET has no target, cannot be marked as spent
- Cannot delete if current_balance > 0
- Negative balance strictly forbidden (withdrawal validation)
```

### Domain Relationships
```
User (1) ──────── (N) Transactions
User (1) ──────── (N) Categories
User (1) ──────── (N) Buckets
Category (1) ──── (N) Transactions
Bucket (1) ────── (N) Transactions
```

---

## Data Flow

### Monthly Model (Critical Design Decision)

**Decision:** Each calendar month is an **independent financial period**.

**Rationale:**
- Simplifies queries (no cross-month calculations)
- Aligns with user mental model (monthly budgeting)
- Opening Balance makes each month self-contained
- Enables future month archival/deletion without affecting other months

### Formula: Monthly Usable Amount
```
Usable = Opening Balance
       + Σ(Income excluding Opening Balance)
       + Σ(Withdrawals)
       - Σ(Expenses)
       - Σ(Investments)

WHERE transaction_date BETWEEN {month_start} AND {month_end}
AND user_id = {current_user}
```

**Note:** `GOAL_COMPLETED` transactions do NOT affect Usable Amount. They only affect Bucket Balance.

### Formula: Bucket Balance (Cumulative)
```
Bucket Balance = Σ(All Investments to Bucket)
               - Σ(All Withdrawals from Bucket)
               - Σ(GOAL_COMPLETED for Bucket)

WHERE bucket_id = {bucket_id}
AND user_id = {current_user}
(No date filter - cumulative across all time)
```

### Month Rollover Logic
```
┌─────────────────────────────────────────────────────────────┐
│ User opens app on January 1, 2025                           │
│                                                             │
│ System checks:                                              │
│   1. Does user have Opening Balance for Jan 2025? → No     │
│   2. Does previous month (Dec 2024) exist? → Yes           │
│   3. Calculate Dec 2024 ending balance → LKR 45,000        │
│                                                             │
│ Action: Show MonthRolloverPrompt                            │
│   "Your December ending balance was LKR 45,000.            │
│    Use this as your opening balance?"                       │
│                                                             │
│ If User Confirms:                                           │
│   → Create Income transaction                               │
│     - type: INCOME                                          │
│     - category: "Opening Balance" (system category)         │
│     - amount: 45,000                                        │
│     - transaction_date: 2025-01-01                          │
│                                                             │
│ If User Skips:                                              │
│   → Prompt reappears on next app open                       │
└─────────────────────────────────────────────────────────────┘
```

---

## Security Architecture

### Authentication Flow (JWT)
```
1. User submits credentials → POST /api/v1/auth/login
2. LoginUseCase validates email/password against DB
3. If valid:
   → Generate JWT with claims: { user_id, email, exp }
   → Generate refresh token (optional, for V2)
   → Return { token, user_id, email }
4. Frontend stores token in Redux + localStorage
5. Axios interceptor attaches token to all requests:
   Authorization: Bearer {token}
```

### Authorization (User Isolation)
```
┌─────────────────────────────────────────────────────────────┐
│              Spring Security Filter Chain                   │
│                                                             │
│ 1. JwtAuthenticationFilter                                  │
│    → Extract JWT from Authorization header                  │
│    → Validate signature + expiration                        │
│    → Extract user_id → set in SecurityContext              │
│                                                             │
│ 2. Controller receives request                              │
│    → @AuthenticationPrincipal AppUser currentUser           │
│                                                             │
│ 3. UseCase validates ownership                              │
│    → Fetch resource by ID                                   │
│    → if (resource.user_id != currentUser.id)                │
│        throw ForbiddenException                             │
│                                                             │
│ 4. Repository queries always include:                       │
│    WHERE user_id = {currentUser.id}                         │
└─────────────────────────────────────────────────────────────┘
```

**Critical Rule:** Every database query MUST filter by `user_id`. No exceptions.

### Security Checklist (Per Endpoint)
- [ ] JWT validation enabled (except /auth/**)
- [ ] user_id extracted from SecurityContext
- [ ] Resource ownership validated before read/update/delete
- [ ] All queries include `WHERE user_id = ?`
- [ ] Input validation with @Valid annotations
- [ ] SQL injection prevented (jOOQ type-safe queries)
- [ ] XSS prevented (React escapes by default)

### CORS Configuration

The `SecurityConfig.java` enables CORS for local development:

| Setting | Value |
|---------|-------|
| **Allowed Origins** | `http://localhost:5173` (Vite), `http://localhost:3000` |
| **Allowed Methods** | GET, POST, PUT, PATCH, DELETE, OPTIONS |
| **Allowed Headers** | Authorization, Content-Type, Accept, Origin, X-Requested-With |
| **Exposed Headers** | Authorization |
| **Credentials** | Enabled (JWT in Authorization header) |
| **Preflight Cache** | 1 hour (3600s) |

**Production Considerations:**
- Update `allowedOrigins` in `SecurityConfig.corsConfigurationSource()` with production frontend URL
- Consider restricting allowed methods per endpoint
- Enable HTTPS-only in production

### Public Endpoints (No Authentication)

| Endpoint | Purpose |
|----------|---------|
| `POST /api/auth/login` | User authentication |
| `POST /api/auth/register` | New user registration |
| `POST /api/auth/refresh` | Token refresh |
| `GET /actuator/**` | Health checks and metrics |
| `OPTIONS /**` | CORS preflight requests |

All other endpoints require valid JWT in `Authorization: Bearer {token}` header.

---

## Database Schema Design

### Schema: `public`

#### Table: `users`
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    currency_symbol VARCHAR(10) DEFAULT 'LKR',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

#### Table: `categories`
```sql
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

CREATE INDEX idx_categories_user_id ON categories(user_id);
CREATE INDEX idx_categories_archived ON categories(user_id, is_archived);
```

**Design Decision:** `UNIQUE(user_id, name, type)` allows same name for different types (e.g., "Other" for both INCOME and EXPENSE).

#### Table: `buckets`
```sql
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

CREATE INDEX idx_buckets_user_id ON buckets(user_id);
CREATE INDEX idx_buckets_status ON buckets(user_id, status);
```

**Design Decision:** CHECK constraint ensures only SAVINGS_GOAL can have target_amount.

#### Table: `transactions`
```sql
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

    CHECK (
        (type IN ('INCOME', 'EXPENSE') AND category_id IS NOT NULL AND bucket_id IS NULL)
        OR
        (type IN ('INVESTMENT', 'WITHDRAWAL', 'GOAL_COMPLETED') AND bucket_id IS NOT NULL AND category_id IS NULL)
    )
);

CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date DESC);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_transactions_bucket ON transactions(bucket_id);
CREATE INDEX idx_transactions_month ON transactions(user_id, EXTRACT(YEAR FROM transaction_date), EXTRACT(MONTH FROM transaction_date));
```

**Design Decisions:**
- `ON DELETE RESTRICT` for category/bucket prevents accidental data loss
- CHECK constraint enforces category/bucket mutual exclusivity
- Composite index on (user_id, year, month) for fast monthly queries
- `transaction_date DESC` for recent-first ordering

#### View: `bucket_balances` (Materialized View Candidate)
```sql
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
```

**Performance Note:** For V2, consider materializing this view and refreshing on transaction changes.

---

## API Design Decisions

### RESTful Conventions
```
GET    /api/v1/transactions       → List (with filters)
POST   /api/v1/transactions       → Create
GET    /api/v1/transactions/{id}  → Read single (not in MVP)
PUT    /api/v1/transactions/{id}  → Update
DELETE /api/v1/transactions/{id}  → Delete

POST   /api/v1/buckets/{id}/mark-spent  → Action endpoint
POST   /api/v1/categories/{id}/merge    → Action endpoint
```

**Decision:** Use POST for actions that aren't pure CRUD (mark-spent, merge).

### Query Parameter Strategy
```
GET /api/v1/transactions?year=2025&month=1
GET /api/v1/dashboard/summary?year=2025&month=1
```

**Decision:** `year` and `month` are REQUIRED parameters for scoped endpoints. This enforces the monthly model and prevents accidental full-table scans.

**Why not optional?**
- Prevents performance issues (scanning all user transactions)
- Makes the monthly scope explicit in the API contract
- Forces frontend to manage selected month state

### Error Response Format
```json
{
  "error": "validation_failed",
  "message": "Category type does not match transaction type",
  "details": {
    "field": "category_id",
    "provided_type": "EXPENSE",
    "expected_type": "INCOME"
  },
  "timestamp": "2025-01-13T10:30:00Z"
}
```

**Decision:** Structured error responses with machine-readable error codes enable better frontend error handling.

---

## State Management Strategy

### Frontend Architecture Decision
```
┌─────────────────────────────────────────────────────────────┐
│                    TanStack Query                           │
│  (Server State - Caching, Refetching, Optimistic Updates)  │
│                                                             │
│  Handles: transactions, categories, buckets, dashboard      │
│                                                             │
│  Benefits:                                                  │
│  - Automatic cache invalidation                             │
│  - Background refetching                                    │
│  - Optimistic updates                                       │
│  - Loading/error states                                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    Redux Toolkit                            │
│         (Client State - Auth, UI, Selected Month)          │
│                                                             │
│  authSlice: { user, token, isAuthenticated }                │
│  uiSlice: { selectedMonth, selectedYear, drawerOpen }       │
│                                                             │
│  Persisted to localStorage via redux-persist                │
└─────────────────────────────────────────────────────────────┘
```

**Rationale:**
- **TanStack Query** excels at server state management (automatic caching, refetching)
- **Redux** excels at client state that needs to persist across sessions
- Separation of concerns: server state vs. client state

### Cache Invalidation Strategy
```typescript
// After creating transaction
queryClient.invalidateQueries(['transactions', year, month])
queryClient.invalidateQueries(['dashboard', year, month])
queryClient.invalidateQueries(['buckets'])

// After marking bucket as spent
queryClient.invalidateQueries(['buckets'])
queryClient.invalidateQueries(['transactions', year, month]) // GOAL_COMPLETED added
queryClient.invalidateQueries(['dashboard', year, month])
```

---

## Key Design Patterns

### 1. Repository Pattern (with Interface Segregation)
```java
// Interface (in UseCase package)
public interface PostTransactionDataAccess {
    Transaction create(Transaction transaction);
    boolean categoryExists(Long categoryId, Long userId);
}

// Implementation (in UseCase package)
@Repository
public class PostTransactionRepository implements PostTransactionDataAccess {
    private final DSLContext dsl;

    @Override
    public Transaction create(Transaction transaction) {
        // jOOQ implementation
    }
}
```

**Pattern:** Each UseCase defines its own minimal DataAccess interface. No "one repository to rule them all".

### 2. Presenter Pattern (Response Building)
```java
// Interface
public interface PostTransactionResponseBuilder {
    PostTransactionResponse build(Transaction transaction);
}

// Implementation
@Component
public class PostTransactionPresenter implements PostTransactionResponseBuilder {
    @Override
    public PostTransactionResponse build(Transaction transaction) {
        return new PostTransactionResponse(
            transaction.id(),
            transaction.amount(),
            formatCurrency(transaction.amount()),
            transaction.transactionDate().format(DATE_FORMATTER)
        );
    }
}
```

**Pattern:** Separates DTO transformation logic from business logic. UseCases don't know about JSON structure.

### 3. Strategy Pattern (Transaction Type Handling)
```java
// Instead of if/else chains, use polymorphism

public sealed interface TransactionEffect
    permits IncomeEffect, ExpenseEffect, InvestmentEffect, WithdrawalEffect {
    void apply(Transaction transaction);
}

// In UseCase
TransactionEffect effect = switch (request.type()) {
    case INCOME -> new IncomeEffect();
    case EXPENSE -> new ExpenseEffect();
    case INVESTMENT -> new InvestmentEffect();
    case WITHDRAWAL -> new WithdrawalEffect();
};
effect.apply(transaction);
```

**Note:** This is an optimization for V2. MVP can use simpler validation.

### 4. Command Pattern (Month Rollover)
```typescript
// Frontend: Encapsulate rollover action
const executeMonthRollover = useMutation({
  mutationFn: async (amount: number) => {
    return api.createTransaction({
      type: 'INCOME',
      category_id: OPENING_BALANCE_CATEGORY_ID,
      amount,
      transaction_date: `${year}-${month}-01`,
    })
  },
  onSuccess: () => {
    queryClient.invalidateQueries(['dashboard', year, month])
  }
})
```

---

## Architectural Constraints & Trade-offs

### Constraint 1: Monthly Scope (No Cross-Month Calculations)
**Decision:** Usable Amount is calculated per month only.

**Trade-off:**
- ✅ Simpler queries, faster performance
- ✅ Each month is self-contained
- ❌ Cannot show "total savings this year" without manual aggregation
- ❌ Requires Opening Balance entry each month

**Rationale:** MVP focuses on monthly budgeting. Yearly analytics are post-MVP.

### Constraint 2: Single Currency Per User
**Decision:** One currency symbol per user, no multi-currency transactions.

**Trade-off:**
- ✅ Eliminates complex exchange rate logic
- ✅ Simpler UX
- ❌ Users with multi-currency income must convert manually

**Rationale:** Target users (Sri Lankan market) primarily use LKR. Multi-currency is V2.

### Constraint 3: No Recurring Transactions
**Decision:** All transactions are manual entry.

**Trade-off:**
- ✅ Simpler backend logic
- ✅ User awareness of every transaction
- ❌ More friction for monthly bills

**Rationale:** Recurring logic adds complexity. MVP focuses on core flow tracking.

### Constraint 4: Bucket Balance is Cash Contributions Only
**Decision:** Buckets track contributions, NOT market value.

**Trade-off:**
- ✅ Simple, predictable math
- ✅ No need for external APIs (stock prices, etc.)
- ❌ Cannot show investment returns

**Rationale:** This is a **cash flow tracker**, not a portfolio manager. Users who want net worth tracking should use different tools.

### Constraint 5: jOOQ Over JPA/Hibernate
**Decision:** Use jOOQ for all database access.

**Trade-off:**
- ✅ Full SQL control, no ORM magic
- ✅ Type-safe queries
- ✅ Explicit performance characteristics
- ❌ More boilerplate than JPA
- ❌ Schema changes require code regeneration

**Rationale:** Financial calculations require precise SQL. Hibernate's lazy loading and N+1 queries are dangerous in this domain.

---

## Architectural Evolution Path

### MVP → V1 (Mobile)
- Backend remains unchanged (stateless JWT API)
- Flutter app consumes existing endpoints
- Consider adding push notifications (new infrastructure)

### V1 → V2 (Advanced Features)
**Potential Additions:**
- Recurring transactions (new table: `recurring_templates`)
- Budgets per category (new table: `category_budgets`)
- Reports/Analytics (new endpoints: `/api/v1/reports/yearly`)
- Multi-currency (add `currency` column to transactions, exchange rate table)

**Refactoring Considerations:**
- Materialized view for bucket_balances (performance)
- Event sourcing for audit trail (compliance)
- CQRS separation (read-heavy dashboard vs. write-heavy transactions)

---

## Appendix: Key Architectural Decisions Log

| Date | Decision | Rationale | Status |
|------|----------|-----------|--------|
| 2025-01-13 | Use jOOQ over JPA | Full SQL control, type safety | ✅ Committed |
| 2025-01-13 | Monthly scope (no cross-month) | Simplicity, performance | ✅ Committed |
| 2025-01-13 | Endpoint-per-package pattern | Feature isolation, maintainability | ✅ Committed |
| 2025-01-13 | TanStack Query + Redux | Best tool for each state type | ✅ Committed |
| 2025-01-13 | Bucket balance = contributions only | Cash flow focus, not net worth | ✅ Committed |
| TBD | Materialized views for balances | Performance optimization | 🔄 Deferred to V2 |
| TBD | Multi-currency support | Market demand | 🔄 Deferred to V2 |

---

## Questions & Answers

**Q: Why not use a single `TransactionService` for all operations?**
A: That creates a God Class with 15+ methods. Endpoint-per-package keeps each operation isolated and testable.

**Q: Why not use Spring Data JPA?**
A: Financial calculations need explicit SQL. JPA's abstractions hide performance characteristics and can generate inefficient queries.

**Q: Why Opening Balance as an Income transaction instead of a separate field?**
A: Makes each month self-contained. User can edit/delete Opening Balance like any transaction. Simplifies the data model.

**Q: Why are GOAL_COMPLETED transactions excluded from Usable Amount?**
A: "Marking as spent" means the money was used for its goal (e.g., bought the car). It's not available for spending. It left the system entirely.

**Q: Why separate Category types (INCOME/EXPENSE) instead of a single list?**
A: Prevents user error (using "Salary" as an expense category). Enforces business rule at schema level.

---

**Document Version:** 1.0
**Last Updated:** 2025-01-13
**Maintained By:** Development Team
