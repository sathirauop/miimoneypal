# Changelog

All notable changes to MiiMoneyPal will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

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
