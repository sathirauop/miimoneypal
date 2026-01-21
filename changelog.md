# Changelog

All notable changes to MiiMoneyPal will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

#### Backend Auth Module (2026-01-21)
- **Auth REST Endpoints** (`rest/auth/` package)
  - `POST /api/auth/register` - User registration with immediate login
    - Email validation (unique, case-insensitive)
    - Password hashing with BCrypt (min 8, max 100 characters)
    - Optional currency symbol (defaults to LKR)
    - Returns user info and JWT tokens (access + refresh)
  - `POST /api/auth/login` - Email/password authentication
    - Authenticates via Spring Security `AuthenticationManager`
    - Returns user info and JWT tokens
  - `POST /api/auth/refresh` - Access token refresh
    - Validates refresh token signature and expiration
    - Verifies token type (rejects access tokens)
    - Verifies user still exists in database
    - Returns new access token (refresh token NOT rotated)
- **Use Cases** (Business Logic)
  - `RegisterUseCase` - Orchestrates user registration flow
    - Email normalization (lowercase + trim)
    - BCrypt password hashing
    - User creation via `UserDataAccess.save()`
    - JWT token generation
    - Marked with `@Transactional` for atomicity
  - `LoginUseCase` - Orchestrates login flow
    - Delegates authentication to `AuthenticationManager`
    - Fetches full user record for response
    - JWT token generation
  - `RefreshUseCase` - Orchestrates token refresh flow
    - Multi-step validation (signature, type, user existence)
    - New access token generation
- **Presenters** (DTO Transformation)
  - `RegisterPresenter` - Transforms `User` domain + tokens → `RegisterResponse`
  - `LoginPresenter` - Transforms `User` domain + tokens → `LoginResponse`
  - `RefreshPresenter` - Transforms access token → `RefreshResponse`
  - All inject token expiration values from `application.properties`
- **Response DTOs** (implementing `ApiResponse`)
  - `RegisterResponse` - userId, email, currencySymbol, accessToken, refreshToken, expirations
  - `LoginResponse` - userId, email, currencySymbol, accessToken, refreshToken, expirations
  - `RefreshResponse` - accessToken, accessTokenExpiresIn
- **Request DTOs** (with validation)
  - `RegisterRequest` - email (@Email), password (@Size), currencySymbol (optional)
  - `LoginRequest` - email (@Email), password (@NotBlank)
  - `RefreshRequest` - refreshToken (@NotBlank)
- **Controller**
  - `AuthController` - Single controller for all auth endpoints
  - Uses `@Valid` for request validation
  - Returns appropriate HTTP status codes (201 CREATED for register, 200 OK for login/refresh)
- **Test Coverage**
  - Unit tests: `RegisterUseCaseTest`, `LoginUseCaseTest`, `RefreshUseCaseTest`
  - Integration test: `AuthControllerIntegrationTest` with Testcontainers
  - Full auth flow test: register → login → refresh
  - Error scenarios: duplicate email (409), invalid credentials (401), validation errors (400)
- **Error Handling**
  - `DuplicateResourceException` → 409 Conflict (duplicate email)
  - `BadCredentialsException` → 401 Unauthorized (invalid login, expired token, user not found)
  - `BadRequestException` → 400 Bad Request (access token used as refresh)
  - Validation errors → 400 Bad Request with field errors
- **Token Configuration**
  - Access token expiration: 24 hours (86400000ms)
  - Refresh token expiration: 7 days (604800000ms)
  - Both configurable via `jwt.access-token-expiration` and `jwt.refresh-token-expiration` in `application.properties`

#### Backend Transaction Module (2026-01-21)
- **Transaction REST Endpoints** (`rest/transactions/` package) - Full CRUD operations
  - `POST /api/transactions` - Create new transaction
    - Validates transaction type (rejects system-generated GOAL_COMPLETED)
    - Enforces category/bucket mutual exclusivity based on type
    - Validates category type matches transaction type (INCOME/EXPENSE)
    - Prevents bucket overdraft for WITHDRAWAL transactions
    - Blocks archived categories and buckets
    - Returns 201 CREATED with transaction details and related entity names
  - `GET /api/transactions/{id}` - Fetch single transaction
    - User-scoped query (404 if not found or doesn't belong to user)
    - Includes related category and bucket details (name, type)
    - Returns formatted amount (currency formatting)
    - Returns 200 OK with enriched transaction data
  - `GET /api/transactions` - List with filters and pagination
    - Filters: type, date range (start/end), category, bucket, note search
    - Pagination: offset/limit (default 20, max 100)
    - Ordered by transaction_date DESC, created_at DESC
    - Case-insensitive note search using PostgreSQL ILIKE
    - Returns paginated response with metadata (hasNext, hasPrevious, totalPages)
    - Efficient batch fetching of related entities (avoids N+1 queries)
  - `PUT /api/transactions/{id}` - Update transaction
    - Transaction type is immutable (cannot be changed)
    - Validates path ID matches request body ID
    - Prevents updating system-generated transactions
    - For WITHDRAWAL: validates balance using delta calculation
    - Returns 200 OK with updated transaction details
  - `DELETE /api/transactions/{id}` - Delete transaction
    - Hard delete (permanent removal)
    - Prevents deleting system-generated transactions
    - Returns 200 OK with confirmation message
- **Shared Repositories**
  - `CategoryDataAccess` + `CategoryRepository` - Category validation and lookup
  - `BucketDataAccess` + `BucketRepository` - Bucket validation and balance calculation
- **Use Cases** (Business Logic) - One per endpoint following Clean Architecture
  - `PostTransactionUseCase` - Creates transactions with validation
    - Rejects GOAL_COMPLETED type (system-generated only)
    - Validates category for INCOME/EXPENSE types
    - Validates bucket for INVESTMENT/WITHDRAWAL types
    - Checks category type matches transaction type
    - Prevents using archived categories/buckets
    - For WITHDRAWAL: validates bucket balance >= amount
  - `GetTransactionUseCase` - Fetches single transaction
    - Read-only transaction (@Transactional(readOnly = true))
    - User-scoped query for security
    - Gracefully handles missing related entities
  - `ListTransactionsUseCase` - Lists with filters
    - Read-only transaction for optimization
    - Dynamic WHERE clause building with jOOQ
    - Separate count query for pagination metadata
  - `PutTransactionUseCase` - Updates transactions
    - Prevents type changes (enforces immutability)
    - Prevents updating GOAL_COMPLETED transactions
    - Delta-based balance validation for WITHDRAWAL updates
    - Preserves audit fields (createdAt, id, userId, type)
  - `DeleteTransactionUseCase` - Deletes transactions
    - Prevents deleting GOAL_COMPLETED transactions
    - Hard delete with user-scoped WHERE clause
- **Presenters** (DTO Transformation)
  - `PostTransactionPresenter` - Formats created transaction
  - `GetTransactionPresenter` - Formats fetched transaction with type details
  - `ListTransactionsPresenter` - Batch fetches related entities, formats list
  - `PutTransactionPresenter` - Formats updated transaction
  - `DeleteTransactionPresenter` - Simple confirmation message
  - All use `NumberFormat.getCurrencyInstance(Locale.US)` for amount formatting
- **Response DTOs** (implementing `ApiResponse`)
  - `PostTransactionResponse` - id, type, amount, formattedAmount, date, category/bucket details, note, createdAt
  - `GetTransactionResponse` - Adds categoryType, bucketType, updatedAt
  - `ListTransactionsResponse` - Paginated with items, page, size, totalItems, totalPages, hasNext, hasPrevious
  - `TransactionSummary` - Lightweight DTO for list items
  - `PutTransactionResponse` - Includes updatedAt timestamp
  - `DeleteTransactionResponse` - message, deletedTransactionId
- **Request DTOs** (with validation)
  - `PostTransactionRequest` - type, amount (@Positive, @Digits), date (@PastOrPresent), categoryId/bucketId, note
  - `GetTransactionRequest` - id (@Positive)
  - `ListTransactionsRequest` - Filters + offset/limit with compact constructor defaults
  - `PutTransactionRequest` - id, amount, date, categoryId/bucketId, note (no type field)
  - `DeleteTransactionRequest` - id (@Positive)
- **Controller**
  - `TransactionController` - Single controller for all CRUD endpoints
  - Uses `@AuthenticationPrincipal AppUser` for user injection
  - Path variable validation for PUT (ensures ID consistency)
  - Query parameter auto-mapping for LIST filters
  - Returns appropriate HTTP status codes (201, 200)
- **Business Rules Enforced**
  - GOAL_COMPLETED transactions are system-generated only (cannot create/update/delete)
  - Category required for INCOME/EXPENSE, bucket required for INVESTMENT/WITHDRAWAL
  - Category/bucket mutual exclusivity (cannot have both)
  - Category type must match transaction type
  - Cannot use archived categories or buckets
  - Bucket balance cannot go negative (validates before WITHDRAWAL)
  - Transaction type is immutable after creation
  - Delta-based balance validation for WITHDRAWAL updates
- **Test Coverage**
  - Unit tests: `PostTransactionUseCaseTest` (7 scenarios), `PutTransactionUseCaseTest` (4 scenarios), `DeleteTransactionUseCaseTest` (3 scenarios)
  - Tests cover: validation rules, balance calculations, system transaction protection, error scenarios
  - Uses Mockito for dependency mocking
  - AssertJ for fluent assertions
- **Security**
  - All endpoints require JWT authentication (configured in SecurityConfig)
  - User-scoped queries in all repositories (user_id in WHERE clause)
  - Path variable validation prevents ID mismatch attacks
  - Balance calculations use real-time database queries (not cached values)

#### Backend Authentication Service (2026-01-20)
- **AppUserDetailsService** (`security/` package)
  - Implements Spring Security's `UserDetailsService` interface
  - Loads users from database via `UserDataAccess.findByEmail()`
  - Converts domain `User` record to `AppUser` (Spring Security's `UserDetails`)
  - Assigns `Role.USER` by default to all authenticated users
  - Throws `UsernameNotFoundException` for invalid credentials
- **AuthenticationManager Bean** (SecurityConfig)
  - `DaoAuthenticationProvider` configured with `AppUserDetailsService`
  - Integrates with `BCryptPasswordEncoder` for password verification
  - Used by login endpoint to authenticate user credentials
  - Enables username/password authentication flow
- **Unit Tests** (`AppUserDetailsServiceTest`)
  - Tests user loading by email
  - Tests exception handling for non-existent users
  - Tests role assignment and authorities
  - Tests account status flags (enabled, non-expired, non-locked)

#### Backend Shared Repository (2026-01-20)
- **UserDataAccess Interface** (`repository/` package)
  - Data access contract following the `*DataAccess` naming convention
  - `findByEmail(String)` - Case-insensitive email lookup, returns `Optional<User>`
  - `findById(Long)` - User lookup by ID, returns `Optional<User>`
  - `save(User)` - Insert new user or update existing, returns saved user with generated ID
  - `existsByEmail(String)` - Efficient email existence check
  - `updateCurrencySymbol(Long, String)` - Update user's currency preference
- **UserRepository** (jOOQ implementation)
  - Implements `UserDataAccess` following the `*Repository` naming convention for implementations
  - Type-safe SQL using jOOQ DSL (no native SQL strings)
  - jOOQ `UsersRecord` to domain `User` record conversion via `toDomainRecord()`
  - Robust duplicate key detection using constraint violation checks
  - `passwordHash` validation in `insert()` to ensure required field before DB operation
  - Exception translation: `DataAccessException` → `DuplicateResourceException`/`ResourceNotFoundException`

#### Backend Domain Records (2026-01-17)
- **Enum Types** (`records/` package)
  - `TransactionType` - INCOME, EXPENSE, INVESTMENT, WITHDRAWAL, GOAL_COMPLETED with balance effect methods
  - `CategoryType` - INCOME, EXPENSE with transaction type matching
  - `BucketType` - SAVINGS_GOAL, PERPETUAL_ASSET with target/mark-as-spent capability checks
  - `BucketStatus` - ACTIVE, ARCHIVED with transaction allowance check
- **Domain Records** (immutable with @Builder, validation in compact constructors)
  - `User` - email validation, default currency (LKR), persistence check, email matching
  - `Category` - user-scoped, type matching with transactions, protected system categories, archive/delete logic
  - `Bucket` - target amount validation (only for SAVINGS_GOAL), positive amount enforcement, status-based transaction checks
  - `Transaction` - category/bucket mutual exclusivity, positive amount enforcement, balance effect calculations, system-generated detection

#### Backend Security Infrastructure (2026-01-17)
- **JWT Authentication System**
  - `JwtTokenProvider` - Access/refresh token generation and validation using jjwt 0.12.6
  - `JwtAuthenticationFilter` - Extracts JWT from Authorization header, builds AppUser from claims
  - `JwtAuthenticationEntryPoint` - Returns JSON 401 response for unauthenticated requests
  - `JwtAccessDeniedHandler` - Returns JSON 403 response for unauthorized requests
- **Security Configuration**
  - `SecurityConfig.java` - Stateless session management, CORS for frontend, public/protected endpoints
  - `PasswordEncoder` bean using BCrypt
  - Filter chain with JWT filter before UsernamePasswordAuthenticationFilter
- **User Model**
  - `AppUser` - Spring Security UserDetails implementation with id, email, role
  - `Role` enum - USER role with associated permissions
  - `Permission` enum - Fine-grained permissions (transaction:read/write, category:read/write, etc.)
- **Core Architecture**
  - `UseCase<REQUEST, RESPONSE>` interface - Base for business logic orchestrators
  - `AuthenticatedUseCase<REQUEST, RESPONSE>` interface - For endpoints requiring user context
  - `ApiResponse` marker interface - Type safety for response DTOs
  - `ErrorResponse` record - Standardized error format with field errors support
  - `OffsetSearchResponse<T>` - Generic paginated response with metadata
- **Exception Handling**
  - `GlobalExceptionHandler` - Centralized exception-to-response mapping
  - `ResourceNotFoundException` - 404 Not Found
  - `BadRequestException` - 400 Bad Request
  - `BusinessRuleException` - 422 Unprocessable Entity
  - `DuplicateResourceException` - 409 Conflict
- **Configuration**
  - `EndPoints.java` - Centralized API route constants
  - `application.properties` - JWT config, Jackson snake_case, logging levels
  - `SecurityConfig` uses `EndPoints` constants (no hardcoded URLs)
- **Dependencies**
  - Added jjwt-api, jjwt-impl, jjwt-jackson 0.12.6 for JWT handling
  - Added spring-boot-starter-validation for bean validation

### Changed

#### Documentation Updates (2026-01-17)
- Updated CLAUDE.md with actual backend folder structure (shows which packages exist vs planned)
- Added Security Configuration section with CORS and public endpoint documentation
- Updated Architecture.md with CORS configuration table and public endpoints list
- Fixed JWT Configuration section (was placeholder, now shows actual implementation)

#### Backend Configuration (2026-01-16)
- jOOQ Gradle plugin (nu.studer.jooq v9.0) configured for type-safe database access
- Custom Flyway migration task using Docker psql (Gradle 9.2.1 compatibility workaround)
- Automatic task chain: `compileJava` → `generateJooq` → `flywayMigrate`
- Database port changed from 5432 to 5433 to avoid conflicts with local PostgreSQL
- Container name standardized to `miimoneypal-postgres-1`
- Generated jOOQ sources excluded from git (`build/generated-src/`)
- jOOQ configuration: Records enabled, POJOs disabled, java.time types enabled

#### Frontend (2026-01-13)
- Vite + React 19 project initialization (`mimoneypal-ui/`)
- Tailwind CSS v4 with custom MiiMoneyPal theme:
  - Transaction type colors (income: green, expense: red, investment: blue, withdrawal: amber)
  - Custom color palette (primary, secondary, background, surface, border)
- State management setup:
  - Redux Toolkit with `authSlice` (login, register, logout, JWT persistence)
  - Redux `uiSlice` (drawer states, filters, month navigation)
  - TanStack Query v5 with query keys factory
- React Router DOM v7 routing:
  - Protected routes with `ProtectedRoute` component
  - Public route: `/login`
  - Protected routes: `/`, `/transactions`, `/buckets`, `/settings`
- Mobile-first layout components:
  - `Layout.jsx` with `h-[100dvh]` viewport handling
  - `BottomNav.jsx` fixed navigation bar
- Feature module structure:
  - `features/auth/` - Login page, authSlice, useAuth hook
  - `features/dashboard/` - Dashboard page with month navigation
  - `features/transactions/` - Transaction list page placeholder
  - `features/buckets/` - Bucket management page placeholder
  - `features/settings/` - Settings page placeholder
- Lib utilities:
  - `axios.js` - Axios instance with JWT interceptors, `/api` proxy
  - `queryClient.js` - TanStack Query config with query keys
  - `utils.js` - `cn()` helper, `formatCurrency()`, `formatDate()`, `getTransactionTypeProps()`
- PWA support via vite-plugin-pwa
- Testing infrastructure:
  - Vitest configuration
  - React Testing Library + Jest DOM
  - Test setup file with localStorage/matchMedia mocks
- npm scripts: `dev`, `build`, `preview`, `lint`, `lint:fix`, `test`, `test:watch`, `test:coverage`

#### Backend (Initial Setup)
- Spring Boot 4.0.1 project scaffolding
- Gradle build configuration with required dependencies:
  - Spring Boot starters (webmvc, security, jooq, flyway)
  - PostgreSQL driver
  - Testcontainers for integration testing
  - Lombok for reducing boilerplate
  - Spring Boot DevTools for development
  - Docker Compose integration
- Docker Compose configuration for local PostgreSQL
- Testcontainers setup with `@ServiceConnection` for auto-configuration
- Test infrastructure with JUnit 5 and Spring test utilities

### Infrastructure
- Backend base package: `com.sathira.miimoneypal`
- Backend directory: `MiiMoneyPal/`
- Frontend directory: `mimoneypal-ui/`
- Java toolchain: 25 (configured in build.gradle)
- Node.js: 18+ required

---

## Version History

No releases yet. Project is in initial development phase.
