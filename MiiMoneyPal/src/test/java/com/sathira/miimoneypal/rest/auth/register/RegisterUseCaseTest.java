package com.sathira.miimoneypal.rest.auth.register;

import com.sathira.miimoneypal.exception.DuplicateResourceException;
import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import com.sathira.miimoneypal.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock private UserDataAccess userDataAccess;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private RegisterResponseBuilder responseBuilder;

    private RegisterUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUseCase(userDataAccess, passwordEncoder, jwtTokenProvider, responseBuilder);
    }

    @Test
    @DisplayName("Should register user with hashed password and return tokens")
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequest request = new RegisterRequest("Test@Example.com", "password123", "USD");
        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .currencySymbol("USD")
                .createdAt(LocalDateTime.now())
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userDataAccess.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(any(AppUser.class))).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(any(AppUser.class))).thenReturn("refreshToken");
        when(responseBuilder.build(savedUser, "accessToken", "refreshToken"))
                .thenReturn(new RegisterResponse(1L, "test@example.com", "USD", "accessToken", "refreshToken", 86400000L, 604800000L));

        // When
        RegisterResponse response = useCase.execute(request);

        // Then
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.accessToken()).isEqualTo("accessToken");

        // Verify email is lowercased and trimmed
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDataAccess).save(userCaptor.capture());
        assertThat(userCaptor.getValue().email()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should use default currency when not specified")
    void shouldUseDefaultCurrency() {
        // Given
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", null);
        User savedUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .currencySymbol("LKR")
                .createdAt(LocalDateTime.now())
                .build();

        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userDataAccess.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh");
        when(responseBuilder.build(any(), any(), any()))
                .thenReturn(new RegisterResponse(1L, "test@example.com", "LKR", "token", "refresh", 86400000L, 604800000L));

        // When
        RegisterResponse response = useCase.execute(request);

        // Then
        assertThat(response.currencySymbol()).isEqualTo("LKR");
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void shouldThrowExceptionForDuplicateEmail() {
        // Given
        RegisterRequest request = new RegisterRequest("existing@example.com", "password123", null);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userDataAccess.save(any(User.class)))
                .thenThrow(new DuplicateResourceException("User", "email", "existing@example.com"));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email");
    }
}
