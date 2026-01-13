# Changelog

All notable changes to MiiMoneyPal will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial Spring Boot 4.0.1 project scaffolding
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
- Base package: `com.sathira.miimoneypal`
- Backend directory: `MiiMoneyPal/`
- Java toolchain: 25 (configured in build.gradle)

---

## Version History

No releases yet. Project is in initial development phase.
