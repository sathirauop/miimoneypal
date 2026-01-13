# Project Status

Last updated: 2025-01-13

## Current Phase: Project Scaffolding Complete

The Spring Boot backend scaffolding has been initialized. The project is ready for feature development.

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

### Test Infrastructure
- [x] JUnit 5 with Jupiter
- [x] Testcontainers with PostgreSQL
- [x] `TestcontainersConfiguration.java` with `@ServiceConnection`
- [x] `TestMiiMoneyPalApplication.java` for IDE testing
- [x] Spring Security test utilities

## In Progress

Nothing currently in progress.

## Not Started

### Backend - Core Infrastructure
- [ ] Database schema design
- [ ] Flyway migrations for core tables (users, transactions, categories, buckets)
- [ ] jOOQ code generation (after migrations)
- [ ] Spring Security configuration (JWT authentication)
- [ ] Global exception handler
- [ ] API response wrapper (`ApiResponse` interface)
- [ ] Base architecture interfaces (`UseCase`, `AuthenticatedUseCase`)
- [ ] `EndPoints.java` constants file
- [ ] Jackson snake_case configuration

### Backend - Feature Modules
- [ ] Auth module (register, login, refresh token)
- [ ] Transactions module (CRUD operations)
- [ ] Categories module (CRUD + soft delete)
- [ ] Buckets module (CRUD + investment/withdrawal)
- [ ] Dashboard module (monthly summary, usable amount)

### Frontend
- [ ] Project initialization with Vite + React
- [ ] Tailwind CSS + Shadcn/UI setup
- [ ] Redux Toolkit configuration
- [ ] TanStack Query setup
- [ ] Axios instance with JWT interceptor
- [ ] Route structure
- [ ] Layout components
- [ ] Auth pages
- [ ] Dashboard page
- [ ] Transaction management pages
- [ ] Bucket management pages
- [ ] Settings page

## Dependencies

```
Backend Dependencies (build.gradle):
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

## Known Issues

None currently.

## Next Steps

1. Design database schema based on project_spec.md
2. Write initial Flyway migrations
3. Run `./gradlew generateJooq` to generate type-safe database classes
4. Implement authentication (JWT-based)
5. Build first vertical slice (e.g., categories CRUD)
