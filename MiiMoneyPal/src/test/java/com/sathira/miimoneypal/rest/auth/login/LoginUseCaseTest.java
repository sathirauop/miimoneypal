package com.sathira.miimoneypal.rest.auth.login;

import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import com.sathira.miimoneypal.security.Role;
import com.sathira.miimoneypal.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDataAccess userDataAccess;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private LoginResponseBuilder responseBuilder;

    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(authenticationManager, userDataAccess, jwtTokenProvider, responseBuilder);
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        // Given
        LoginRequest request = new LoginRequest("Test@Example.com", "password123");
        AppUser appUser = AppUser.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(Role.USER)
                .build();
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .currencySymbol("LKR")
                .build();
        Authentication auth = mock(Authentication.class);

        when(auth.getPrincipal()).thenReturn(appUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userDataAccess.findById(1L)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(appUser)).thenReturn("accessToken");
        when(jwtTokenProvider.generateRefreshToken(appUser)).thenReturn("refreshToken");
        when(responseBuilder.build(user, "accessToken", "refreshToken"))
                .thenReturn(new LoginResponse(1L, "test@example.com", "LKR", "accessToken", "refreshToken", 86400000L, 604800000L));

        // When
        LoginResponse response = useCase.execute(request);

        // Then
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.accessToken()).isEqualTo("accessToken");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid password")
    void shouldThrowExceptionForInvalidCredentials() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
