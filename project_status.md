# Project Status

**Last Updated**: 2026-01-21
**Analyzed By**: Claude Code

## Current Phase: Transaction Module Complete

Backend Auth and Transaction modules are now complete. The project has full CRUD operations for transactions with comprehensive business rule validation and test coverage. Next steps: implement Categories and Buckets modules.

## Completed

### Backend Infrastructure
- [x] Spring Boot 4.0.1 project initialized
- [x] Gradle build configuration with all dependencies
- [x] Docker Compose setup for PostgreSQL (port 5433)
- [x] Spring Boot Docker Compose integration (auto-starts DB)
- [x] Testcontainers configured for integration testing
- [x] Flyway migration framework installed
- [x] jOOQ dependency installed
- [x] **jOOQ Gradle plugin configured (nu.studer.jooq v9.0)**
- [x] **jOOQ code generation working** (generates to `build/generated-src/jooq/main/`)
- [x] **Custom Flyway migration task** (Docker psql-based, Gradle 9.2.1 compatible)
- [x] **Task chain configured:** `compileJava` → `generateJooq` → `flywayMigrate`
- [x] Spring Security dependency installed
- [x] Lombok configured
- [x] Bean validation dependency (spring-boot-starter-validation)

### Backend Security Infrastructure (NEW)
- [x] **JWT dependencies** (jjwt-api 0.12.6, jjwt-impl, jjwt-jackson)
- [x] **SecurityConfig.java** - Stateless JWT filter chain, CORS configuration
- [x] **JwtTokenProvider** - Access/refresh token generation and validation
- [x] **JwtAuthenticationFilter** - Token extraction from Authorization header
- [x] **JwtAuthenticationEntryPoint** - 401 Unauthorized handler
- [x] **JwtAccessDeniedHandler** - 403 Forbidden handler
- [x] **AppUser** - Spring Security UserDetails implementation
- [x] **Role enum** - USER role with permissions
- [x] **Permission enum** - Fine-grained permissions (transaction:read, etc.)
- [x] **application.properties** - JWT config, Jackson snake_case, logging

### Backend Core Architecture (NEW)
- [x] **UseCase interface** - Base interface for business logic orchestrators
- [x] **AuthenticatedUseCase interface** - Use cases requiring user context
- [x] **ApiResponse interface** - Marker interface for response DTOs
- [x] **ErrorResponse record** - Standard error format with field errors
- [x] **OffsetSearchResponse** - Generic paginated response
- [x] **EndPoints.java** - Centralized API route constants
- [x] **GlobalExceptionHandler** - Consistent exception-to-response mapping
- [x] **Custom exceptions** - ResourceNotFoundException, BadRequestException, BusinessRuleException, DuplicateResourceException
- [x] **Exception handlers** - Handles validation errors, security exceptions (BadCredentials, AccessDenied), and catch-all for unexpected errors

### Backend Domain Records (NEW)
- [x] **Enum types** - TransactionType, CategoryType, BucketType, BucketStatus
- [x] **User record** - Immutable domain record with validation and helper methods
- [x] **Category record** - Category domain record with type matching and archival logic
- [x] **Bucket record** - Bucket domain record with target validation and status checks
- [x] **Transaction record** - Transaction domain record with balance effect calculations

### Backend Shared Repository (NEW)
- [x] **UserDataAccess interface** - Contract for user data access (findByEmail, findById, save, existsByEmail, updateCurrencySymbol)
- [x] **UserRepository** - jOOQ implementation with domain record conversion
- [x] **Duplicate key detection** - Robust constraint violation handling
- [x] **Password validation** - Validates passwordHash required for new user registration

### Backend Authentication Service (NEW)
- [x] **AppUserDetailsService** - Spring Security UserDetailsService implementation for database authentication
- [x] **AuthenticationManager bean** - DaoAuthenticationProvider configured with UserDetailsService and BCrypt
- [x] **User loading** - Loads users from database via UserDataAccess, converts to AppUser
- [x] **Role assignment** - Assigns USER role with full permissions to all loaded users
- [x] **Unit tests** - Comprehensive test coverage for AppUserDetailsService

### Backend - Feature Modules
- [x] **Auth module (rest/auth/)** - Complete authentication system with three endpoints:
  - [x] **POST /api/auth/register** - User registration with immediate login
  - [x] **POST /api/auth/login** - Email/password authentication
  - [x] **POST /api/auth/refresh** - Access token refresh
  - [x] **RegisterUseCase** - Business logic for user registration with BCrypt hashing
  - [x] **LoginUseCase** - Business logic for authentication via AuthenticationManager
  - [x] **RefreshUseCase** - Business logic for token refresh with validation
  - [x] **RegisterPresenter** - Transforms User domain to RegisterResponse DTO
  - [x] **LoginPresenter** - Transforms User domain to LoginResponse DTO
  - [x] **RefreshPresenter** - Transforms access token to RefreshResponse DTO
  - [x] **Unit tests** - RegisterUseCaseTest, LoginUseCaseTest, RefreshUseCaseTest
  - [x] **Integration tests** - AuthControllerIntegrationTest with Testcontainers
  - [x] **Email normalization** - Case-insensitive email handling (lowercase + trim)
  - [x] **Token expiration** - Access token (24h), Refresh token (7 days)
  - [x] **Error handling** - DuplicateResourceException (409), BadCredentialsException (401), validation errors (400)

- [x] **Transaction module (rest/transactions/)** - Complete CRUD operations for financial transactions:
  - [x] **POST /api/transactions** - Create new transaction with validation
  - [x] **GET /api/transactions/{id}** - Fetch single transaction with details
  - [x] **GET /api/transactions** - List with filters and pagination
  - [x] **PUT /api/transactions/{id}** - Update transaction (type immutable)
  - [x] **DELETE /api/transactions/{id}** - Delete transaction (hard delete)
  - [x] **Shared repositories** - CategoryRepository, BucketRepository for validation
  - [x] **PostTransactionUseCase** - Creates transactions with business rule enforcement
  - [x] **GetTransactionUseCase** - Fetches single transaction (read-only)
  - [x] **ListTransactionsUseCase** - Lists with dynamic filtering and pagination
  - [x] **PutTransactionUseCase** - Updates with delta-based balance validation
  - [x] **DeleteTransactionUseCase** - Deletes with system transaction protection
  - [x] **Presenters** - Currency formatting and batch entity fetching
  - [x] **TransactionController** - Single controller with @AuthenticationPrincipal
  - [x] **Unit tests** - PostTransactionUseCaseTest (7 scenarios), PutTransactionUseCaseTest (4 scenarios), DeleteTransactionUseCaseTest (3 scenarios)
  - [x] **Business rules** - System transaction protection, category/bucket mutual exclusivity, balance validation, type immutability
  - [x] **Security** - User-scoped queries, JWT authentication required

### Test Infrastructure (Backend)
- [x] JUnit 5 with Jupiter
- [x] Testcontainers with PostgreSQL
- [x] `TestcontainersConfiguration.java` with `@ServiceConnection`
- [x] `TestMiiMoneyPalApplication.java` for IDE testing
- [x] Spring Security test utilities

### Frontend Infrastructure

#### Build & Development Tools
- [x] Vite 7.2.4 configured as build tool
- [x] React 19.2.0 with React DOM
- [x] ESLint with React Hooks and React Refresh plugins
- [x] Development scripts configured (dev, build, preview, lint)
- [x] Path aliases configured (`@/` maps to `src/`)
- [x] API proxy configured (`/api` -> `http://localhost:8080`)

#### Styling System
- [x] Tailwind CSS v4 with `@tailwindcss/vite` plugin
- [x] Custom theme in `index.css` using `@theme` directive
- [x] MiiMoneyPal color palette defined (primary, secondary, semantic colors)
- [x] Transaction type colors (income green, expense red, investment blue, withdrawal amber)
- [x] Mobile-first utilities (`h-[100dvh]`, scrollbar-hide)
- [x] `clsx` and `tailwind-merge` for class composition (cn utility)

#### State Management
- [x] Redux Toolkit v2.11.2 configured
- [x] Redux store with typed hooks (useAppDispatch, useAppSelector)
- [x] Auth slice (login, register, logout, fetchCurrentUser thunks)
- [x] UI slice (drawer states, transaction filters, date navigation)
- [x] Redux DevTools enabled in development

#### Server State Management
- [x] TanStack Query v5 installed and configured
- [x] QueryClient with sensible defaults (5min staleTime, 30min gcTime)
- [x] Query keys factory for type-safe cache invalidation
- [x] Query keys structured by domain (dashboard, transactions, buckets, categories, user)
- [x] React Query DevTools configured

#### Routing & Navigation
- [x] React Router DOM v7 configured
- [x] BrowserRouter with nested routes
- [x] ProtectedRoute wrapper for auth-gated pages
- [x] Layout component with Outlet for route rendering
- [x] BottomNav component for mobile navigation
- [x] Routes defined: `/login` (public), `/`, `/transactions`, `/buckets`, `/settings` (protected)

#### HTTP Client
- [x] Axios v1.13.2 configured
- [x] Axios instance with `/api` baseURL
- [x] Request interceptor attaching JWT from localStorage
- [x] Response interceptor handling 401 errors (auto-logout)

#### Forms & Validation
- [x] React Hook Form v7.71.0 installed
- [x] Zod v4.3.5 for schema validation
- [x] `@hookform/resolvers` for Zod integration

#### PWA Support
- [x] vite-plugin-pwa v1.2.0 configured
- [x] PWA manifest defined (name, theme color, icons)
- [x] Auto-update registration type
- [x] Offline assets configuration

### Frontend Structure

#### Directory Organization
```
src/
├── features/          # Feature modules (vertical slices)
│   ├── auth/         # Login, authSlice, useAuth
│   ├── dashboard/    # Dashboard page
│   ├── transactions/ # Transactions page
│   ├── buckets/      # Buckets page
│   ├── settings/     # Settings page
│   └── categories/   # Empty (not started)
├── components/
│   ├── Layout/       # Layout, BottomNav, ProtectedRoute
│   └── ui/           # Empty (Shadcn components not added yet)
├── lib/              # Shared utilities
│   ├── axios.js      # HTTP client config
│   ├── queryClient.js # TanStack Query setup
│   └── utils.js      # cn, formatCurrency, formatDate, getTransactionTypeProps
├── store/            # Redux configuration
│   ├── index.js      # Store config
│   ├── hooks.js      # Typed hooks
│   └── uiSlice.js    # UI state slice
├── test/
│   └── setup.js      # Test mocks (localStorage, matchMedia)
├── App.jsx           # Root component with providers
├── main.jsx          # Entry point
└── index.css         # Tailwind + custom theme
```

#### Implemented Features (Scaffolding Only)
- [x] Auth module structure (Login.jsx, authSlice.js, useAuth.js)
- [x] Dashboard placeholder page
- [x] Transactions placeholder page
- [x] Buckets placeholder page
- [x] Settings placeholder page
- [x] Layout system with mobile-first design
- [x] ProtectedRoute component with auth checks
- [x] BottomNav for mobile navigation

#### Utility Functions
- [x] `cn()` - Tailwind class merging
- [x] `formatCurrency()` - Intl currency formatter
- [x] `formatDate()` - Intl date formatter
- [x] `formatRelativeTime()` - Relative time strings
- [x] `getTransactionTypeProps()` - Transaction type metadata

### Test Infrastructure (Frontend)
- [x] Vitest v4.0.17 configured with jsdom environment
- [x] React Testing Library v16.3.1 installed
- [x] Jest DOM v6.9.1 matchers
- [x] Test setup file with localStorage and matchMedia mocks
- [x] Test scripts (test, test:watch, test:coverage)
- [x] Coverage configured with v8 provider (text, json, html reporters)
- [x] Test pattern: `src/**/*.{test,spec}.{js,jsx}`

## In Progress

Nothing currently in progress.

## Not Started

### Backend - Remaining Infrastructure

**What STILL NEEDS TO BE CREATED:**
- [x] ~~Domain records (records/ package - User, Transaction, Category, Bucket records)~~ ✅ COMPLETED
- [x] ~~Repository layer code (repository/ package - shared repositories)~~ ✅ COMPLETED
- [x] ~~UserDetailsService implementation for loading users from database~~ ✅ COMPLETED

### Backend - Feature Modules

**STATUS**: Auth and Transaction modules complete. Remaining feature modules need implementation.

- [x] ~~Auth module (rest/auth/ - register, login, refresh token)~~ ✅ COMPLETED
- [x] ~~Transactions module (rest/transactions/ - CRUD operations)~~ ✅ COMPLETED
- [ ] Categories module (rest/categories/ - CRUD + soft delete)
- [ ] Buckets module (rest/buckets/ - CRUD + investment/withdrawal)
- [ ] Dashboard module (rest/dashboard/ - monthly summary, usable amount)

### Frontend - Component Library
- [ ] Shadcn/UI components installation
- [ ] Button component
- [ ] Input, Select, Textarea components
- [ ] Card, Dialog, Drawer components
- [ ] Form components integration with React Hook Form
- [ ] Toast/notification system
- [ ] Loading spinners and skeletons

### Frontend - Feature Implementation
- [ ] Auth API integration (connect authSlice to backend endpoints)
- [ ] Login form with validation (Zod schema)
- [ ] Register form (if required)
- [ ] Dashboard API integration
  - [ ] Monthly summary data display
  - [ ] Usable amount calculation
  - [ ] Month navigation
  - [ ] Opening/closing balance display
- [ ] Transaction CRUD functionality
  - [ ] Transaction list with infinite scroll/pagination
  - [ ] Add transaction form (Income/Expense/Investment/Withdrawal)
  - [ ] Edit transaction
  - [ ] Delete transaction
  - [ ] Transaction filters (type, category, bucket, date range)
  - [ ] Transaction search
- [ ] Category management
  - [ ] Category list
  - [ ] Add/Edit category
  - [ ] Archive category (soft delete)
  - [ ] Category merge functionality
- [ ] Bucket management
  - [ ] Bucket list with balance display
  - [ ] Add/Edit bucket (SAVINGS_GOAL vs PERPETUAL_ASSET)
  - [ ] Investment/Withdrawal forms
  - [ ] Progress bar for SAVINGS_GOAL
  - [ ] "Mark as Spent" action for SAVINGS_GOAL
  - [ ] Archive bucket
- [ ] Settings functionality
  - [ ] User profile display/edit
  - [ ] Currency selection
  - [ ] Theme toggle (if dark mode)
  - [ ] Account deletion

### Frontend - UX/Polish
- [ ] Error handling and toast notifications
- [ ] Loading states for all async operations
- [ ] Empty states for lists
- [ ] Form validation error messages
- [ ] Optimistic updates for mutations
- [ ] Responsive design refinements (tablet, desktop)
- [ ] Animations and transitions
- [ ] Offline support (PWA caching strategies)
- [ ] PWA icons and manifest assets

### Frontend - Testing
- [ ] Auth flow tests
- [ ] Component unit tests
- [ ] Integration tests for forms
- [ ] E2E tests with Playwright (critical user flows)

## Dependencies

### Backend (build.gradle)
```
- spring-boot-starter-flyway
- spring-boot-starter-jooq
- spring-boot-starter-security
- spring-boot-starter-webmvc
- postgresql (runtime)
- flyway-database-postgresql
- spring-boot-devtools
- spring-boot-docker-compose
- lombok
- Testcontainers (postgresql, junit-jupiter)
- Spring test utilities
```

### Frontend (package.json)
```
Dependencies:
- react ^19.2.0
- react-dom ^19.2.0
- @tanstack/react-query ^5.90.16
- @reduxjs/toolkit ^2.11.2
- react-redux ^9.2.0
- react-router-dom ^7.12.0
- react-hook-form ^7.71.0
- @hookform/resolvers ^5.2.2
- zod ^4.3.5
- axios ^1.13.2

DevDependencies:
- vite ^7.2.4
- tailwindcss ^4.1.18
- @tailwindcss/vite ^4.1.18
- vitest ^4.0.17
- @testing-library/react ^16.3.1
- @testing-library/jest-dom ^6.9.1
- vite-plugin-pwa ^1.2.0
- clsx + tailwind-merge (for cn utility)
```

## Frontend Infrastructure Status Summary

### Completed Infrastructure
The frontend scaffolding is **100% complete** for infrastructure setup:
- Build tooling, linting, and development environment configured
- Styling system with Tailwind v4 and custom theme fully operational
- State management with Redux Toolkit and TanStack Query properly configured
- Routing with protected routes and layout system implemented
- HTTP client with JWT authentication ready
- Testing framework configured
- PWA support configured

### Not Started
- **Shadcn/UI components** - The `components/ui/` directory is empty. No UI components have been added yet.
- **Feature implementations** - All feature pages (Dashboard, Transactions, Buckets, Settings) are placeholder files with no real functionality.
- **API integration** - Auth slice has thunks defined but no actual backend connection yet.
- **Forms and validation** - React Hook Form and Zod are installed but no forms or schemas have been implemented.
- **Test files** - Test setup is complete, but no actual test files exist (no `*.test.js` or `*.spec.js` files found).

### Missing Configuration
- **Tailwind config file** - Using Tailwind v4's new `@theme` directive in `index.css` instead of `tailwind.config.js`. This is correct for Tailwind v4, but non-standard compared to v3.
- **Categories feature** - The `src/features/categories/` directory exists but is completely empty.

## Known Issues

### RESOLVED (Previously Critical)
1. ~~**jOOQ generation not configured**~~ - RESOLVED: nu.studer.jooq v9.0 plugin configured and working
2. ~~**Flyway tasks not exposed**~~ - RESOLVED: Custom flywayMigrate task using Docker psql
3. ~~**No Spring Security configuration**~~ - RESOLVED: Full JWT security infrastructure implemented

### HIGH Priority Issues
1. ~~**No REST endpoints**~~ - RESOLVED: Auth module completed with register, login, refresh endpoints
2. **No Shadcn components** - The `components/ui/` directory is empty. No UI components installed despite being required for all forms and UI.
3. **Frontend has 0% real functionality** - All feature pages are empty placeholder components with no API integration, no forms, no data display.

### MEDIUM Priority Issues
4. **No actual tests** - Test infrastructure exists but no test files written (no *.test.js, *.test.java files for business logic)
5. **Categories feature empty** - The `src/features/categories/` directory exists but contains no files
6. ~~**No UserDetailsService**~~ - RESOLVED: AppUserDetailsService implemented with database authentication support

## Next Steps (Critical Path)

### ~~Phase 1: Unblock Backend Development~~ ✅ COMPLETED
- jOOQ Gradle plugin configured and working
- Custom Flyway migration task implemented
- Database schema created and jOOQ code generated

### ~~Phase 2: Build Core Architecture~~ ✅ COMPLETED
- UseCase and AuthenticatedUseCase interfaces created
- ApiResponse, ErrorResponse, OffsetSearchResponse implemented
- GlobalExceptionHandler with custom exceptions
- EndPoints constants file created
- Jackson snake_case configured in application.properties

### ~~Phase 3: Implement Security~~ ✅ COMPLETED
- AppUser, Role, Permission enums created
- JwtTokenProvider, JwtAuthenticationFilter implemented
- SecurityConfig with stateless session management
- CORS configured for frontend origins

### ~~Phase 4: First Feature - Auth~~ ✅ COMPLETED
1. ~~**Create User domain record**~~ ✅ COMPLETED - `records/user/User.java`
2. ~~**Build UserRepository**~~ ✅ COMPLETED - jOOQ-based repository for user lookup by email
3. ~~**Build login endpoint**~~ ✅ COMPLETED - rest/auth/login/ with full vertical slice pattern
4. ~~**Build register endpoint**~~ ✅ COMPLETED - rest/auth/register/ with password hashing
5. ~~**Build refresh endpoint**~~ ✅ COMPLETED - rest/auth/refresh/ for token renewal
6. ~~**Test auth endpoints**~~ ✅ COMPLETED - Comprehensive unit and integration tests

### Phase 5: Frontend Connection (CURRENT PRIORITY)
7. **Install Shadcn components** - Button, Input, Card, Dialog, Label, Form
8. **Build Login form** - Connect to backend /api/auth/login with validation
9. **Test full auth flow** - Register → Login → JWT stored → Protected routes accessible

### Phase 6: First Full Vertical Slice (MEDIUM)
10. **Build Categories CRUD backend** - rest/categories/ with all endpoints
11. **Build Categories CRUD frontend** - Category list, add/edit forms
12. **Test end-to-end** - Verify full CRUD cycle

### Phase 7: Remaining Features (LOWER)
13. Transactions module (most complex - needs categories and buckets)
14. Buckets module with investment/withdrawal logic
15. Dashboard with monthly calculations
16. Settings page
17. Test coverage
18. PWA offline support and polish

## Estimated Effort Remaining

- ~~**Backend core architecture**: 8-12 hours~~ ✅ COMPLETED
- ~~**Spring Security + JWT**: 6-8 hours~~ ✅ COMPLETED
- ~~**Auth feature (first vertical slice)**: 4-6 hours~~ ✅ COMPLETED
- **Categories CRUD**: 6-8 hours
- **Transactions CRUD**: 12-16 hours
- **Buckets + Dashboard**: 10-14 hours
- **Frontend Shadcn + forms**: 8-12 hours
- **Frontend feature integration**: 20-30 hours
- **Testing + polish**: 15-20 hours

**TOTAL ESTIMATED**: 71-106 hours remaining for MVP (down from 75-110)

## Project Health Assessment

- **Infrastructure**: GREEN (scaffolding complete, dependencies installed)
- **Backend Security**: GREEN (JWT authentication, CORS, exception handling complete)
- **Backend Features**: GREEN (Auth and Transaction modules complete with tests, Categories and Buckets pending)
- **Frontend Implementation**: YELLOW (structure exists, 0% functionality)
- **Database**: GREEN (schema designed, migrations applied, jOOQ generated)
- **Overall Status**: GREEN - Auth and Transaction modules complete with comprehensive test coverage and business rules enforcement, ready for Categories and Buckets modules
