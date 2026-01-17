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
- **Build Tool:** Gradle with Groovy DSL
- **Migrations:** Flyway (write SQL migrations BEFORE jOOQ code generation)
- **Security:** Spring Security + JWT (stateless)
- **Testing:** Testcontainers with PostgreSQL, JUnit 5
- **Dev Tools:** Spring Boot DevTools, Docker Compose integration

### Frontend
- **Build Tool:** Vite 7
- **Framework:** React 19
- **Language:** JavaScript (ES6+)
- **State Management:** TanStack Query v5 (server state) + Redux Toolkit (auth/UI state)
- **Styling:** Tailwind CSS v4 + Shadcn/UI (Radix Primitives)
- **Routing:** React Router DOM v7
- **Forms:** React Hook Form + Zod validation
- **PWA:** vite-plugin-pwa
- **Testing:** Vitest + React Testing Library

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
# Port: 5433 (mapped from container's 5432 to avoid local PostgreSQL conflicts)
```

**Important:** The Gradle tasks use a hardcoded container name: `miimoneypal-postgres-1`
- This is the default Docker Compose naming pattern: `{project}-{service}-{index}`
- If your project folder has a different name, tasks will fail
- To verify container name: `docker ps --format "{{.Names}}" | grep postgres`

**3. Running Migrations & jOOQ Generation**
```bash
# Generate jOOQ code (automatically runs migrations first via task chain)
./gradlew generateJooq

# Task chain: compileJava → generateJooq → flywayMigrate
# You don't need to manually run flywayMigrate before generateJooq
```

**Note:** The `flywayMigrate` task is a custom Docker-based task (not the standard Flyway Gradle plugin) due to Gradle 9.2.1 compatibility. It uses `docker exec` with psql to apply migrations.

**4. Frontend Setup**
```bash
# Navigate to frontend directory
cd mimoneypal-ui

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

# Generate jOOQ code (automatically runs flywayMigrate first)
# Requires: PostgreSQL container running on port 5433
./gradlew generateJooq

# Run Flyway migrations manually (custom Docker psql task)
# Note: This is called automatically by generateJooq
./gradlew flywayMigrate

# Clean build (removes generated jOOQ code - will regenerate on next build)
./gradlew clean build
```

### Frontend (run from mimoneypal-ui/ directory)
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
│   ├── architecture/                    # Base interfaces (UseCase, AuthenticatedUseCase) ✅
│   ├── config/                          # Spring configurations ✅
│   │   └── SecurityConfig.java          # JWT filter chain, CORS, BCrypt encoder
│   ├── constants/                       # EndPoints.java (centralized URL paths) ✅
│   ├── exception/                       # GlobalExceptionHandler, custom exceptions ✅
│   ├── models/response/                 # ApiResponse, ErrorResponse, OffsetSearchResponse ✅
│   ├── rest/                            # Feature modules (vertical slices) - NOT YET CREATED
│   │   ├── auth/                        # Public auth endpoints (login, register, refresh)
│   │   ├── admin/                       # Admin-only features
│   │   └── {feature}/                   # Other public features
│   │       └── {action}/                # Endpoint package (post/get/put/delete)
│   ├── security/                        # Security infrastructure ✅
│   │   ├── AppUser.java                 # Spring Security UserDetails implementation
│   │   ├── Role.java                    # USER role with permissions
│   │   ├── Permission.java              # Fine-grained permissions enum
│   │   ├── JwtAuthenticationEntryPoint.java  # 401 handler
│   │   ├── JwtAccessDeniedHandler.java       # 403 handler
│   │   └── jwt/                         # JWT token handling
│   │       ├── JwtTokenProvider.java    # Token generation/validation
│   │       └── JwtAuthenticationFilter.java  # Extract JWT from requests
│   ├── records/{domain}/                # Domain entities - NOT YET CREATED
│   ├── repository/                      # Global repositories - NOT YET CREATED
│   ├── service/                         # Shared services - NOT YET CREATED (for V2)
│   ├── client/                          # External service clients - NOT YET CREATED (for V2)
│   └── cache/                           # Spring Cache abstractions - NOT YET CREATED (for V2)
├── src/main/resources/
│   ├── application.properties           # JWT config, Jackson snake_case, logging
│   └── db/migration/                    # Flyway SQL migrations (V1__, V2__, etc.)
├── src/test/java/com/sathira/miimoneypal/
│   ├── MiiMoneyPalApplicationTests.java # Integration test base
│   ├── TestMiiMoneyPalApplication.java  # Test runner with Testcontainers
│   └── TestcontainersConfiguration.java # PostgreSQL container config
├── compose.yaml                         # Docker Compose for local PostgreSQL (port 5433)
└── build.gradle                         # Gradle build configuration
```

**Note:** Packages marked "NOT YET CREATED" are planned for future implementation. Packages marked ✅ exist and are implemented.

### 5. Frontend Folder Structure
```
mimoneypal-ui/src/
├── features/               # Business logic modules
│   ├── auth/               # authSlice.js, useAuth.js, Login.jsx
│   ├── dashboard/          # Dashboard.jsx + components
│   ├── transactions/       # Transactions.jsx + CRUD components
│   ├── buckets/            # Buckets.jsx + management components
│   ├── categories/         # Category management (used in forms)
│   └── settings/           # Settings.jsx + user preferences
├── components/
│   ├── ui/                 # Shadcn components (Button, Input, Card)
│   └── Layout/             # Layout.jsx, BottomNav.jsx, ProtectedRoute.jsx
├── lib/
│   ├── axios.js            # Axios instance + JWT interceptors
│   ├── queryClient.js      # TanStack Query config + query keys
│   └── utils.js            # Utility functions (cn, formatCurrency, formatDate)
├── store/                  # Redux store (index.js, hooks.js, uiSlice.js)
├── test/                   # Test setup and utilities
└── App.jsx                 # Route definitions + providers
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
   - Axios instance with JWT interceptor in `lib/axios.js`
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
2. **API Integration:** Create `api.js` with Axios calls
3. **State Management:**
   - TanStack Query hooks for server state
   - Redux slice if auth/UI state needed
4. **Build Components:** Page components and sub-components
5. **Add Routes:** Register in `App.jsx`
6. **Update Navigation:** Add to `BottomNav.jsx` if needed
7. **Write Tests:** Component tests with React Testing Library + Vitest

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
- CORS is configured in `SecurityConfig.java` for frontend origins (localhost:5173, localhost:3000)

## Package Naming Convention

Use this structure for actual implementation:
- **Backend base package:** `com.sathira.miimoneypal`
- **Backend directory:** `MiiMoneyPal/`
- **Frontend directory:** `mimoneypal-ui/`
- **Frontend features:** Follow the structure in "Frontend Folder Structure" section above

## Environment Configuration

### Backend (application.properties)
```properties
# Current minimal config - Spring Boot Docker Compose handles database connection
spring.application.name=MiiMoneyPal

# Database connection is auto-configured via Docker Compose integration
# The @ServiceConnection annotation in TestcontainersConfiguration handles this

# Add these for production/explicit configuration:
# spring.datasource.url=jdbc:postgresql://localhost:5433/mydatabase
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
      - '5433:5432'  # Host:Container (5433 to avoid conflicts with local PostgreSQL)
```

### jOOQ Configuration (build.gradle)
```groovy
// Database connection properties used by jOOQ and Flyway tasks
ext {
    dbUrl = 'jdbc:postgresql://localhost:5433/mydatabase'
    dbUser = 'myuser'
    dbPassword = 'secret'
    dbName = 'mydatabase'
    containerName = 'miimoneypal-postgres-1'
}

// jOOQ generates code to:
// - Directory: build/generated-src/jooq/main/
// - Package: com.sathira.miimoneypal.jooq
// - Excluded: flyway_schema_history table
// - Settings: Records enabled, POJOs disabled, java.time types enabled
```

### JWT Configuration (application.properties)
```properties
# JWT secret - can be overridden via JWT_SECRET environment variable
# IMPORTANT: Use a secure 256-bit (32+ characters) secret in production
# Generate with: openssl rand -base64 32
jwt.secret=${JWT_SECRET:your-development-secret-key-min-256-bits-required}

# Token expiration times (milliseconds)
jwt.access-token-expiration=86400000     # 24 hours
jwt.refresh-token-expiration=604800000   # 7 days

# Jackson JSON Configuration (snake_case for API consistency)
spring.jackson.property-naming-strategy=SNAKE_CASE
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.deserialization.fail-on-unknown-properties=false
```

### Security Configuration (SecurityConfig.java)
```
Public Endpoints (no authentication required):
- /api/auth/login
- /api/auth/register
- /api/auth/refresh
- /actuator/** (health checks)

CORS Configuration:
- Allowed Origins: http://localhost:5173 (Vite), http://localhost:3000
- Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- Allowed Headers: Authorization, Content-Type, Accept, Origin, X-Requested-With
- Credentials: Enabled (for JWT in Authorization header)
- Preflight Cache: 1 hour (3600s)

Session Management: STATELESS (no server-side sessions)
Password Encoding: BCrypt
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
