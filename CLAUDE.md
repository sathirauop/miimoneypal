# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MiiMoneyPal is a personal finance application for tracking monthly cash flow (Income vs. Expenses) and long-term asset accumulation (Investments). The core philosophy is "Control the Flow" - tracking how money moves In (Income), Out (Expenses), and into Buckets (Investments/Goals).

**Primary Metric:** Usable Amount = (Initial Balance + Σ Income + Σ Withdrawals) - (Σ Expenses + Σ Investments)

This is a **Modular Monolithic** architecture organized by Feature, not by technical layer.

## Documentation

This repository maintains several documentation files to track project evolution and architectural decisions:

- **[Architecture.md](./Architecture.md)** - Detailed architectural decisions, design patterns, and system diagrams. Consult this when making significant architectural changes or understanding the rationale behind current patterns.

- **[changelog.md](./changelog.md)** - Chronological record of all notable changes, features added, bugs fixed, and breaking changes. Update this file when completing significant features or making user-facing changes.

- **[project_status.md](./project_status.md)** - Current implementation status, what's completed, what's in progress, and what's planned. Check this file to understand the current state of the project before starting new work.

Refer to **[project_spec.md](./project_spec.md)** for the complete project specification including business requirements, formulas, and detailed technical specifications.

## Tech Stack

### Backend
- **Language:** Java 21 (LTS with Virtual Threads and Records)
- **Framework:** Spring Boot 4.0.1
- **Database:** PostgreSQL 16
- **Data Access:** jOOQ (type-safe SQL DSL) - NO native SQL strings
- **Build Tool:** Gradle
- **Migrations:** Flyway (write SQL migrations BEFORE jOOQ code generation)
- **Security:** Spring Security + JWT (stateless)

### Frontend
- **Build Tool:** Vite
- **Framework:** React 18
- **Language:** JavaScript (ES6+)
- **State Management:** TanStack Query v5 (server state) + Redux Toolkit (auth/UI state)
- **Styling:** Tailwind CSS + Shadcn/UI (Radix Primitives)
- **Routing:** React Router DOM v6
- **Forms:** React Hook Form + Zod validation
- **PWA:** vite-plugin-pwa

## Development Commands

### Backend
```bash
# Build the project
./gradlew build

# Run application
./gradlew bootRun

# Run tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.organization.project.rest.admin.event.post.PostEventUseCaseTest"

# Generate jOOQ code (after Flyway migration)
./gradlew generateJooq

# Run Flyway migrations
./gradlew flywayMigrate
```

### Frontend
```bash
# Install dependencies
npm install

# Run development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint
```

## Architecture Principles

### 1. Vertical Slicing (Feature-First Organization)
Code is organized by **business domain** (e.g., transactions, buckets, auth), NOT by technical layer (controllers, services, repositories).

### 2. Endpoint-Per-Package Pattern
Each REST endpoint gets its own dedicated package at `rest/{feature}/{action}/` containing:

**Always Required:**
- `{Verb}{Noun}Request.java` - Input DTO with @Valid annotations
- `{Verb}{Noun}Response.java` - Output DTO implementing ApiResponse
- `{Verb}{Noun}UseCase.java` - Business logic orchestrator (injects interfaces, not classes)
- `{Verb}{Noun}DataAccess.java` - Interface for database operations
- `{Verb}{Noun}Repository.java` - jOOQ/JPA implementation
- `{Verb}{Noun}ResponseBuilder.java` - Interface for DTO transformation
- `{Verb}{Noun}Presenter.java` - Implements transformation logic

**Optional (Only When Needed):**
- `{Verb}{Noun}HttpDataAccess.java` - Interface for external APIs (S3/R2, payment gateways, email)
- `{Verb}{Noun}HttpRepository.java` - Implementation with SDK/REST clients

### 3. Clean Architecture Dependencies
- Controllers → UseCases (via interfaces)
- UseCases → DataAccess/ResponseBuilder interfaces (NEVER concrete implementations)
- Repositories → jOOQ DSLContext (NEVER native SQL strings)
- Presenters → Domain Records

### 4. Backend Folder Structure
```
com.organization.project/
├── architecture/           # Base interfaces (UseCase, AuthenticatedUseCase)
├── rest/                   # Feature modules (vertical slices)
│   ├── auth/               # Public auth endpoints
│   ├── admin/              # Admin-only features
│   └── {feature}/          # Other public features
│       └── {action}/       # Endpoint package (post/get/put/delete)
├── security/               # AppUser, Role, Permission, JWT logic
├── config/                 # Spring configurations, properties
├── exception/              # GlobalExceptionHandler, custom exceptions
├── models/response/        # ApiResponse interface, OffsetSearchResponse
├── records/{domain}/       # Domain entities (immutable, shared across endpoints)
├── repository/             # Global repositories (shared by 3+ endpoints)
├── service/                # Shared services (cross-cutting concerns only)
├── client/                 # External service clients (S3/R2)
├── cache/                  # Spring Cache abstractions
└── constants/              # EndPoints.java (centralized URL paths)
```

### 5. Frontend Folder Structure
```
src/
├── features/               # Business logic modules
│   ├── auth/               # Auth API, authSlice, Login.tsx, useAuth.ts
│   ├── dashboard/          # Dashboard page + components
│   ├── transactions/       # CRUD + TransactionList/History
│   ├── buckets/            # Bucket management
│   ├── categories/         # Category management
│   └── settings/           # User settings
├── components/
│   ├── ui/                 # Shadcn components (Button, Input, Card)
│   └── Layout/             # Layout, BottomNav, ProtectedRoute
├── lib/
│   ├── axios.ts            # Axios instance + JWT interceptors
│   ├── queryClient.ts      # TanStack Query config
│   └── utils.ts            # Utility functions (cn, formatCurrency)
├── store/                  # Redux store config + typed hooks
└── App.tsx                 # Route definitions
```

## Critical Implementation Rules

### Backend

1. **Controllers are THIN** - No business logic. Only routing + validation trigger + delegate to UseCase.

2. **UseCases define "What", not "How"**
   - Inject interfaces via constructor (`@RequiredArgsConstructor`)
   - Use `@Transactional` for atomic DB operations (multiple writes, read+write consistency)
   - DO NOT use `@Transactional` for read-only operations or external API calls
   - NEVER build response DTOs manually - delegate to Presenter

3. **Repositories use jOOQ DSL**
   - Inject `DSLContext`
   - NEVER use native SQL strings (prevents injection)
   - Write Flyway migrations BEFORE running jOOQ code generation

4. **Presenters handle transformation logic**
   - Date formatting, presigned URL generation, DTO mapping
   - Keeps UseCases clean and focused on business rules

5. **Domain Records are immutable**
   - Located in `records/{domain}/`
   - Use `@Builder` for flexible construction
   - Mirror database schema structure
   - NEVER expose directly in API responses (always transform via Presenter)

6. **Repository Organization**
   - **Local Repository (90%):** Lives in endpoint package, specific to one operation
   - **Global Repository (10%):** Lives in `repository/`, shared by 3+ endpoints (e.g., UserRepository)

7. **JSON Serialization**
   - Database uses snake_case
   - API uses snake_case
   - Java uses camelCase
   - Configure `PropertyNamingStrategies.SNAKE_CASE` in Jackson

8. **Security Rules**
   - Every API request must validate `user_id` in JWT matches data owner
   - A user can NEVER access another user's data
   - Use `@PreAuthorize("hasAuthority('PERMISSION_NAME')")` on Controllers/UseCases

### Frontend

1. **State Management Strategy**
   - **Server State:** TanStack Query (transactions, categories, buckets, dashboard)
   - **Auth State:** Redux + localStorage (token persistence)
   - **UI State:** Redux (drawer open/close, active filters)

2. **Mobile-First Layout**
   - Use `h-[100dvh]` for viewport height
   - Fixed bottom navigation with `pb-20` padding on scrollable content
   - Responsive drawers (bottom sheet on mobile, modal on desktop)

3. **Route Protection**
   - Wrap protected routes with `<ProtectedRoute>` + `<Layout>`
   - Public routes: `/login`
   - Protected routes: `/`, `/transactions`, `/buckets`, `/settings`

4. **API Integration**
   - Axios instance with JWT interceptor in `lib/axios.ts`
   - Proxy `/api` to `http://localhost:8080` in Vite config
   - Invalidate TanStack Query cache after mutations

## Domain-Specific Logic

### Transaction Types
| Type | Direction | Effect on Usable Amount | Effect on Bucket Balance |
|------|-----------|-------------------------|--------------------------|
| Income | In | Increases (+) | None |
| Expense | Out | Decreases (-) | None |
| Investment | Transfer Out | Decreases (-) | Increases (+) |
| Withdrawal | Transfer In | Increases (+) | Decreases (-) |

### Bucket Types
- **SAVINGS_GOAL:** Optional target amount, "Mark as Spent" action (archives bucket, creates GOAL_COMPLETED withdrawal)
- **PERPETUAL_ASSET:** No target, no "Mark as Spent", can only delete if balance is 0

### Deletion Policy
- **No transactions exist:** Hard delete permitted
- **Transactions exist:** Soft delete (archive - hidden from dropdowns, visible in history)
- **User can:** Archive or Merge into another category/bucket

### Balance Calculations
- **Usable Amount:** Cumulative running total across all time (NOT reset monthly)
- **Monthly Dashboard:** Shows opening balance (00:00 on 1st), closing balance (current or end of month), net change
- **Negative Usable Amount:** Allowed (show red warning)
- **Negative Bucket Balance:** Strictly blocked

## Anti-Patterns to Avoid

- ❌ Creating "God" services (e.g., EventService with 20 methods) → ✅ Use focused UseCases
- ❌ Injecting Repositories into Controllers → ✅ Inject UseCases
- ❌ Building Response DTOs in UseCases → ✅ Delegate to Presenters
- ❌ Using `@Transactional` on external API calls → ✅ Only for DB operations
- ❌ Hardcoding URLs → ✅ Use EndPoints constants
- ❌ Over-engineering solutions → ✅ Keep it simple, only implement what's requested
- ❌ Adding unnecessary abstractions → ✅ Three similar lines are better than premature abstraction
- ❌ Using backwards-compatibility hacks → ✅ Delete unused code completely

## Testing Strategy

### Backend
- **Unit Tests:** Test UseCases with mocked dependencies (Repository, Presenter)
  - Location: `src/test/java/{package}/rest/{feature}/{action}/`
- **Integration Tests:** Use `@DataJpaTest` or TestContainers for PostgreSQL
- **Controller Tests:** Use `@WebMvcTest` with MockMvc

### Frontend
- Test business logic in hooks and utility functions
- Integration tests with React Testing Library
- E2E tests for critical user flows (login, add transaction, create bucket)

## New Feature Checklist

When adding a new REST endpoint, create:

**Required:**
- [ ] Package: `rest/{feature}/{action}/`
- [ ] Request Record with @Valid
- [ ] Response Record implementing ApiResponse
- [ ] DataAccess Interface
- [ ] Repository (jOOQ/JPA implementation)
- [ ] ResponseBuilder Interface
- [ ] Presenter
- [ ] UseCase
- [ ] Controller method
- [ ] Endpoint constant in EndPoints.java
- [ ] Security rule in SecurityConfig.java
- [ ] Unit tests for UseCase

**Optional:**
- [ ] HttpDataAccess Interface (for external APIs)
- [ ] HttpRepository (S3/R2/payment gateways)
- [ ] Domain Record in `records/{domain}/`
- [ ] Global Repository (if used by 3+ endpoints)
- [ ] Shared Service (if cross-cutting concern)
- [ ] Cache (for expensive operations)
- [ ] Flyway migration

## Important Notes

- The backend is designed to be **stateless** and **API-first** to support the future Flutter mobile app (V1)
- Flyway migrations must be written BEFORE jOOQ code generation
- Use `@ConditionalOnProperty` for optional beans (R2/S3 repositories)
- All response DTOs must implement the `ApiResponse` marker interface
- Caching should use Spring Cache abstraction (`@Cacheable`) for expensive operations
- CORS must be configured in `WebConfig.java` for frontend origin
