package com.sathira.miimoneypal.rest.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test for Auth module.
 * Verifies that the Spring application context loads successfully with all auth components.
 *
 * Full HTTP integration tests can be run manually using tools like curl or Postman.
 */
@SpringBootTest
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Should load application context successfully with auth module")
    void contextLoads() {
        // If this test passes, it means all auth components are properly wired:
        // - AuthController
        // - RegisterUseCase, LoginUseCase, RefreshUseCase
        // - RegisterPresenter, LoginPresenter, RefreshPresenter
        // - UserRepository, AppUserDetailsService
        // - JwtTokenProvider, SecurityConfig
        // - Database connection via Testcontainers
    }
}
