# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MiiMoneyPal is a personal finance application for tracking monthly cash flow (Income vs. Expenses) and long-term asset accumulation (Investments). The core philosophy is "Control the Flow" - tracking how money moves In (Income), Out (Expenses), and into Buckets (Investments/Goals).

**Primary Metric:** Usable Amount = (Initial Balance + Σ Income + Σ Withdrawals) - (Σ Expenses + Σ Investments)

This is a **Modular Monolithic** architecture organized by Feature, not by technical layer.

**Current Status:** This is a greenfield project. Check [project_status.md](./project_status.md) for current implementation progress.

## Documentation

This repository maintains several documentation files to track project evolution and architectural decisions:

- **[Architecture.md](./Architecture.md)** - Detailed architectural decisions, design patterns, and system diagrams. Consult this when making significant architectural changes or understanding the rationale behind current patterns.

- **[changelog.md](./changelog.md)** - Chronological record of all notable changes, features added, bugs fixed, and breaking changes. Update this file when completing significant features or making user-facing changes.

- **[project_status.md](./project_status.md)** - Current implementation status, what's completed, what's in progress, and what's planned. Check this file to understand the current state of the project before starting new work.

Refer to **[project_spec.md](./project_spec.md)** for the complete project specification including business requirements, formulas, and detailed technical specifications.

## Tech Stack

### Backend
- **Language:** Java 25 (configured in build.gradle toolchain)
- **Framework:** Spring Boot 4.0.1
- **Database:** PostgreSQL (latest via Docker)
- **Data Access:** jOOQ (type-safe SQL DSL) - NO native SQL strings
- **Build Tool:** Gradle with Kotlin DSL
- **Migrations:** Flyway (write SQL migrations BEFORE jOOQ code generation)
- **Security:** Spring Security + JWT (stateless)
- **Testing:** Testcontainers with PostgreSQL, JUnit 5
- **Dev Tools:** Spring Boot DevTools, Docker Compose integration

### Frontend
- **Build Tool:** Vite
- **Framework:** React 18
- **Language:** JavaScript (ES6+)
- **State Management:** TanStack Query v5 (server state) + Redux Toolkit (auth/UI state)
- **Styling:** Tailwind CSS + Shadcn/UI (Radix Primitives)
- **Routing:** React Router DOM v6
- **Forms:** React Hook Form + Zod validation
- **PWA:** vite-plugin-pwa

## Getting Started

### Prerequisites
- Java 25 JDK (or compatible version)
- Node.js 18+ and npm
- Docker and Docker Compose (required for PostgreSQL)

### Initial Setup

**1. Backend Setup**
```bash
# Navigate to backend directory
cd MiiMoneyPal

# Start the application (Docker Compose starts PostgreSQL automatically)
./gradlew bootRun

# The Spring Boot Docker Compose integration will:
# - Start PostgreSQL container from compose.yaml
# - Wait for database to be ready
# - Configure datasource automatically via @ServiceConnection
```

**2. Database Credentials (compose.yaml)**
```yaml
# Default Docker Compose PostgreSQL settings:
# Database: mydatabase
# Username: myuser
# Password: secret
# Port: 5432
```

**3. Running Migrations & jOOQ Generation**
```bash
# After writing Flyway migrations in src/main/resources/db/migration/
./gradlew flywayMigrate

# Generate jOOQ code (after migrations)
./gradlew generateJooq
```

**4. Frontend Setup**
```bash
# Navigate to frontend directory (once created)
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

## Development Commands

### Backend (run from MiiMoneyPal/ directory)
```bash
# Build the project
./gradlew build

# Run application (default port: 8080)
# Note: Spring Boot Docker Compose integration starts PostgreSQL automatically
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run tests (uses Testcontainers - requires Docker)
./gradlew test

# Run specific test class
./gradlew test --tests "com.sathira.miimoneypal.rest.transactions.post.PostTransactionUseCaseTest"

# Run tests with coverage
./gradlew test jacocoTestReport

# Generate jOOQ code (run after any Flyway migration)
./gradlew generateJooq

# Run Flyway migrations
./gradlew flywayMigrate

# Rollback last migration
./gradlew flywayUndo

# Clean build
./gradlew clean build

# Check for dependency updates
./gradlew dependencyUpdates
```

### Frontend
```bash
# Install dependencies
npm install

# Run development server (default port: 5173)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint

# Fix linting issues
npm run lint:fix

# Run type checking (if using TypeScript)
npm run type-check

# Run tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
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
MiiMoneyPal/                             # Backend root directory
├── src/main/java/com/sathira/miimoneypal/
│   ├── MiiMoneyPalApplication.java      # Spring Boot entry point
│   ├── architecture/                    # Base interfaces (UseCase, AuthenticatedUseCase)
│   ├── rest/                            # Feature modules (vertical slices)
│   │   ├── auth/                        # Public auth endpoints
│   │   ├── admin/                       # Admin-only features
│   │   └── {feature}/                   # Other public features
│   │       └── {action}/                # Endpoint package (post/get/put/delete)
│   ├── security/                        # AppUser, Role, Permission, JWT logic
│   ├── config/                          # Spring configurations, properties
│   ├── exception/                       # GlobalExceptionHandler, custom exceptions
│   ├── models/response/                 # ApiResponse interface, OffsetSearchResponse
│   ├── records/{domain}/                # Domain entities (immutable, shared across endpoints)
│   ├── repository/                      # Global repositories (shared by 3+ endpoints)
│   ├── service/                         # Shared services (cross-cutting concerns only)
│   ├── client/                          # External service clients (S3/R2)
│   ├── cache/                           # Spring Cache abstractions
│   └── constants/                       # EndPoints.java (centralized URL paths)
├── src/main/resources/
│   ├── application.properties           # Main configuration
│   └── db/migration/                    # Flyway SQL migrations (V1__, V2__, etc.)
├── src/test/java/com/sathira/miimoneypal/
│   ├── MiiMoneyPalApplicationTests.java # Integration test base
│   ├── TestMiiMoneyPalApplication.java  # Test runner with Testcontainers
│   └── TestcontainersConfiguration.java # PostgreSQL container config
├── compose.yaml                         # Docker Compose for local PostgreSQL
└── build.gradle                         # Gradle build configuration
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
  - Location: `src/test/java/com/sathira/miimoneypal/rest/{feature}/{action}/`
- **Integration Tests:** Use Testcontainers with PostgreSQL (already configured)
  - `TestcontainersConfiguration.java` provides PostgreSQL container
  - `@ServiceConnection` auto-configures datasource
- **Controller Tests:** Use `@WebMvcTest` with MockMvc
  - `spring-boot-starter-webmvc-test` dependency included

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

## Implementation Workflow

When starting a new feature, follow this order:

### Backend Feature Implementation
1. **Database First:** Write Flyway migration in `MiiMoneyPal/src/main/resources/db/migration/`
2. **Generate Code:** Run `./gradlew generateJooq` to generate type-safe database classes
3. **Create Domain Record:** Add immutable record in `records/{domain}/`
4. **Create Endpoint Package:** Create `rest/{feature}/{action}/` directory
5. **Implement in Order:**
   - Request/Response records
   - DataAccess interface
   - Repository implementation
   - ResponseBuilder interface
   - Presenter implementation
   - UseCase with business logic
   - Controller method
6. **Register Endpoint:** Add to `constants/EndPoints.java`
7. **Configure Security:** Add rules to `SecurityConfig.java`
8. **Write Tests:** Unit tests for UseCase, integration tests for Repository
9. **Update Documentation:** Add notes to `Architecture.md` if making architectural decisions

### Frontend Feature Implementation
1. **Create Feature Module:** Add directory under `src/features/{feature}/`
2. **API Integration:** Create `api.ts` with Axios calls
3. **State Management:**
   - TanStack Query hooks for server state
   - Redux slice if auth/UI state needed
4. **Build Components:** Page components and sub-components
5. **Add Routes:** Register in `App.tsx`
6. **Update Navigation:** Add to `BottomNav.tsx` if needed
7. **Write Tests:** Component tests with React Testing Library

### Critical Development Workflow Rules
- **ALWAYS** write Flyway migration before jOOQ generation
- **ALWAYS** run `generateJooq` after any migration
- **ALWAYS** invalidate TanStack Query cache after mutations
- **NEVER** commit generated jOOQ code (add to `.gitignore`)
- **NEVER** skip writing tests for UseCases

## Important Notes

- The backend is designed to be **stateless** and **API-first** to support the future Flutter mobile app (V1)
- Flyway migrations must be written BEFORE jOOQ code generation
- **Docker is required** - Spring Boot Docker Compose integration auto-starts PostgreSQL
- **Testcontainers** handles test database - no manual test DB setup needed
- Use `@ConditionalOnProperty` for optional beans (R2/S3 repositories)
- All response DTOs must implement the `ApiResponse` marker interface
- Caching should use Spring Cache abstraction (`@Cacheable`) for expensive operations
- CORS must be configured in `WebConfig.java` for frontend origin

## Package Naming Convention

Use this structure for actual implementation:
- **Backend base package:** `com.sathira.miimoneypal`
- **Backend directory:** `MiiMoneyPal/`
- **Frontend features:** Follow the structure in "Frontend Folder Structure" section above

## Environment Configuration

### Backend (application.properties)
```properties
# Current minimal config - Spring Boot Docker Compose handles database connection
spring.application.name=MiiMoneyPal

# Database connection is auto-configured via Docker Compose integration
# The @ServiceConnection annotation in TestcontainersConfiguration handles this

# Add these for production/explicit configuration:
# spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
# spring.datasource.username=myuser
# spring.datasource.password=secret
```

### Docker Compose (compose.yaml)
```yaml
services:
  postgres:
    image: 'postgres:latest'
    environment:
      - 'POSTGRES_DB=mydatabase'
      - 'POSTGRES_PASSWORD=secret'
      - 'POSTGRES_USER=myuser'
    ports:
      - '5432:5432'
```

### JWT Configuration (to be added)
```yaml
jwt:
  secret: your-dev-secret-key-min-256-bits
  expiration: 86400000  # 24 hours in milliseconds
```

### Frontend (.env.local)
```bash
VITE_API_URL=http://localhost:8080/api
```

## Testing Infrastructure

The project uses **Testcontainers** for integration testing:

```java
// TestcontainersConfiguration.java - Pre-configured
@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }
}
```

**Key Test Classes:**
- `MiiMoneyPalApplicationTests.java` - Base integration test with `@SpringBootTest`
- `TestMiiMoneyPalApplication.java` - Run app with Testcontainers from IDE
- `TestcontainersConfiguration.java` - PostgreSQL container bean definition

**Running Tests:**
```bash
# All tests (requires Docker running)
./gradlew test

# From IDE: Run TestMiiMoneyPalApplication to start app with test database
```
