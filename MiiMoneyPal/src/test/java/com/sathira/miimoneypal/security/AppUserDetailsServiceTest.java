package com.sathira.miimoneypal.security;

import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AppUserDetailsService.
 * Tests user loading and conversion to Spring Security UserDetails.
 */
@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserDataAccess userDataAccess;

    @InjectMocks
    private AppUserDetailsService appUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("$2a$10$hashedPassword")
                .currencySymbol("LKR")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should load user by email successfully")
    void loadUserByUsername_whenUserExists_returnsAppUser() {
        // Given
        when(userDataAccess.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails result = appUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertThat(result).isInstanceOf(AppUser.class);
        AppUser appUser = (AppUser) result;
        assertThat(appUser.getId()).isEqualTo(1L);
        assertThat(appUser.getEmail()).isEqualTo("test@example.com");
        assertThat(appUser.getPassword()).isEqualTo("$2a$10$hashedPassword");
        assertThat(appUser.getRole()).isEqualTo(Role.USER);

        verify(userDataAccess).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_whenUserNotFound_throwsException() {
        // Given
        when(userDataAccess.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() ->
                        appUserDetailsService.loadUserByUsername("nonexistent@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nonexistent@example.com");

        verify(userDataAccess).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should assign USER role to loaded user")
    void loadUserByUsername_assignsUserRole() {
        // Given
        when(userDataAccess.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails result = appUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        AppUser appUser = (AppUser) result;
        assertThat(appUser.getRole()).isEqualTo(Role.USER);
        assertThat(appUser.getAuthorities())
                .extracting("authority")
                .contains("ROLE_USER", "transaction:read", "transaction:write");
    }

    @Test
    @DisplayName("Should return enabled account")
    void loadUserByUsername_returnsEnabledAccount() {
        // Given
        when(userDataAccess.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails result = appUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
    }

    @Test
    @DisplayName("Should use email as username")
    void loadUserByUsername_usesEmailAsUsername() {
        // Given
        User userWithDifferentEmail = User.builder()
                .id(2L)
                .email("user@domain.com")
                .passwordHash("$2a$10$hashedPassword")
                .currencySymbol("LKR")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userDataAccess.findByEmail("user@domain.com"))
                .thenReturn(Optional.of(userWithDifferentEmail));

        // When
        UserDetails result = appUserDetailsService.loadUserByUsername("user@domain.com");

        // Then
        assertThat(result.getUsername()).isEqualTo("user@domain.com");
    }

    @Test
    @DisplayName("Should handle case-insensitive email lookup via UserDataAccess")
    void loadUserByUsername_caseInsensitiveEmail() {
        // Given
        String mixedCaseEmail = "Test@Example.COM";
        when(userDataAccess.findByEmail(mixedCaseEmail))
                .thenReturn(Optional.of(testUser));

        // When
        UserDetails result = appUserDetailsService.loadUserByUsername(mixedCaseEmail);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@example.com"); // Actual stored email

        verify(userDataAccess).findByEmail(mixedCaseEmail);
    }
}
