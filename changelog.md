# Changelog

All notable changes to MiiMoneyPal will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

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
