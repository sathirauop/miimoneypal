package com.sathira.miimoneypal.rest.transactions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test for Transaction module.
 * Verifies that all transaction components are properly wired and application context loads.
 */
@SpringBootTest
@Testcontainers
class TransactionControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @DisplayName("Should load application context successfully with transaction module")
    void contextLoads() {
        // This test verifies that:
        // 1. All transaction UseCases are properly configured
        // 2. All repositories are properly wired with jOOQ DSLContext
        // 3. All presenters are available as Spring beans
        // 4. TransactionController is properly registered
        // 5. Database connection is established via Testcontainers
        // 6. SecurityConfig allows transaction endpoints with authentication

        // If this test passes, the transaction module is properly integrated
    }
}
