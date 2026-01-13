# Project Status

**Last Updated**: 2026-01-14
**Analyzed By**: Project Status Analyzer Agent

## Current Phase: Frontend Scaffolding Complete

Both backend and frontend scaffolding are now complete. The project is ready for feature development.

## Completed

### Backend Infrastructure
- [x] Spring Boot 4.0.1 project initialized
- [x] Gradle build configuration with all dependencies
- [x] Docker Compose setup for PostgreSQL
- [x] Spring Boot Docker Compose integration (auto-starts DB)
- [x] Testcontainers configured for integration testing
- [x] Flyway migration framework installed
- [x] jOOQ dependency installed
- [x] Spring Security dependency installed
- [x] Lombok configured

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

### Backend - Core Infrastructure

**CRITICAL FINDING**: Only `MiiMoneyPalApplication.java` exists in the entire backend codebase. NO business logic has been implemented.

**What EXISTS:**
- [ ] Flyway migrations written (V1__create_tables.sql, V2__create_views.sql)
- [ ] Database schema fully designed with constraints and indexes

**What DOES NOT EXIST:**
- [ ] jOOQ code generation plugin configuration (BLOCKS ALL DATA ACCESS)
- [ ] Spring Security configuration (SecurityConfig.java - app is completely unsecured)
- [ ] Global exception handler (exception/ package does not exist)
- [ ] API response wrapper (`ApiResponse` interface - models/response/ does not exist)
- [ ] Base architecture interfaces (`UseCase`, `AuthenticatedUseCase` - architecture/ does not exist)
- [ ] `EndPoints.java` constants file (constants/ package does not exist)
- [ ] Jackson snake_case configuration
- [ ] Domain records (records/ package does not exist - no User, Transaction, Category, Bucket records)
- [ ] Any repository layer code (repository/ package does not exist)
- [ ] Any configuration classes (config/ package does not exist)
- [ ] Security package (security/ package does not exist - no JWT, no AppUser, no Role)

### Backend - Feature Modules

**CRITICAL**: The `rest/` package does not exist. NO REST endpoints have been implemented.

- [ ] Auth module (rest/auth/ - register, login, refresh token)
- [ ] Transactions module (rest/transactions/ - CRUD operations)
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

### CRITICAL Blockers
1. **jOOQ generation not configured** - The `nu.studer.jooq` Gradle plugin is NOT in build.gradle. Without this, `./gradlew generateJooq` command does not exist and NO type-safe database access code can be generated. This blocks ALL backend data access implementation.
2. **Flyway tasks not exposed** - The Flyway Gradle plugin is not explicitly configured, so `./gradlew flywayMigrate` and related commands are not available.
3. **Zero backend business logic** - The entire backend consists of ONLY MiiMoneyPalApplication.java. No packages exist for rest/, records/, repository/, security/, config/, exception/, constants/, models/, service/, client/, or cache/.

### HIGH Priority Issues
4. **No Spring Security configuration** - Application is completely unsecured. No JWT implementation, no SecurityConfig.java, no authentication filters.
5. **No Shadcn components** - The `components/ui/` directory is empty. No UI components installed despite being required for all forms and UI.
6. **Frontend has 0% real functionality** - All feature pages are empty placeholder components with no API integration, no forms, no data display.

### MEDIUM Priority Issues
7. **No actual tests** - Test infrastructure exists but no test files written (no *.test.js, *.test.java files for business logic)
8. **Categories feature empty** - The `src/features/categories/` directory exists but contains no files
9. **No Gradle task verification** - Cannot confirm if Flyway migrations have been run or if database schema exists in actual PostgreSQL instance

## Next Steps (Critical Path)

### Phase 1: Unblock Backend Development (CRITICAL)
1. **Add jOOQ Gradle plugin to build.gradle** - Add `nu.studer.jooq` plugin with database connection config
2. **Add Flyway Gradle plugin configuration** - Expose flywayMigrate, flywayClean, flywayInfo tasks
3. **Run Flyway migrations** - Execute `./gradlew flywayMigrate` to create database schema
4. **Run jOOQ generation** - Execute `./gradlew generateJooq` to create type-safe DB classes
5. **Verify generated code** - Check that generated jOOQ classes exist in build/generated-src/jooq/

### Phase 2: Build Core Architecture (CRITICAL)
6. **Create architecture package** - Add UseCase and AuthenticatedUseCase interfaces
7. **Create models/response package** - Add ApiResponse interface, OffsetSearchResponse
8. **Create exception package** - Add GlobalExceptionHandler, custom exception classes
9. **Create constants package** - Add EndPoints.java with API route constants
10. **Create records package** - Add User, Transaction, Category, Bucket domain records
11. **Configure Jackson** - Add PropertyNamingStrategies.SNAKE_CASE in WebConfig or application.properties

### Phase 3: Implement Security (CRITICAL)
12. **Create security package** - Add AppUser, Role, Permission enums
13. **Create JWT infrastructure** - JwtTokenProvider, JwtAuthenticationFilter, JwtAuthenticationEntryPoint
14. **Create SecurityConfig.java** - Configure HTTP security, password encoder, JWT filters
15. **Create UserRepository** - First repository using jOOQ for user authentication

### Phase 4: First Feature - Auth (HIGH)
16. **Build auth module** - rest/auth/login/, rest/auth/register/ with full vertical slice
17. **Test auth endpoints** - Use Postman/curl to verify login/register work
18. **Update SecurityConfig** - Add auth endpoints to public access list

### Phase 5: Frontend Connection (HIGH)
19. **Install Shadcn components** - At minimum: Button, Input, Card, Dialog, Label, Form
20. **Build Login form** - Connect to backend /auth/login endpoint with validation
21. **Test full auth flow** - Register → Login → JWT stored → Protected routes accessible

### Phase 6: First Full Vertical Slice (MEDIUM)
22. **Build Categories CRUD backend** - rest/categories/ with all endpoints (POST, GET, PUT, DELETE, PATCH archive)
23. **Build Categories CRUD frontend** - Category list, add/edit forms, archive functionality
24. **Test end-to-end** - Verify full CRUD cycle works from UI → API → Database

### Phase 7: Remaining Features (LOWER)
25. Transactions module (most complex - needs categories and buckets)
26. Buckets module with investment/withdrawal logic
27. Dashboard with monthly calculations
28. Settings page
29. Test coverage
30. PWA offline support and polish

## Estimated Effort Remaining

- **Backend core architecture**: 8-12 hours
- **Spring Security + JWT**: 6-8 hours
- **Auth feature (first vertical slice)**: 4-6 hours
- **Categories CRUD**: 6-8 hours
- **Transactions CRUD**: 12-16 hours
- **Buckets + Dashboard**: 10-14 hours
- **Frontend Shadcn + forms**: 8-12 hours
- **Frontend feature integration**: 20-30 hours
- **Testing + polish**: 15-20 hours

**TOTAL ESTIMATED**: 90-130 hours remaining for MVP

## Project Health Assessment

- **Infrastructure**: GREEN (scaffolding complete, dependencies installed)
- **Backend Implementation**: RED (0% - only main class exists)
- **Frontend Implementation**: YELLOW (structure exists, 0% functionality)
- **Database**: GREEN (schema designed, migrations written, ready to run)
- **Security**: RED (0% - completely unsecured)
- **Overall Status**: RED - Project has solid foundation but needs full implementation
