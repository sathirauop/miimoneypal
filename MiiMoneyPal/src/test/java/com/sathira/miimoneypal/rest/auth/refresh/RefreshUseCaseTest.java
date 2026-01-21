package com.sathira.miimoneypal.rest.auth.refresh;

import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import com.sathira.miimoneypal.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshUseCaseTest {

    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserDataAccess userDataAccess;
    @Mock private RefreshResponseBuilder responseBuilder;

    private RefreshUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RefreshUseCase(jwtTokenProvider, userDataAccess, responseBuilder);
    }

    @Test
    @DisplayName("Should refresh access token with valid refresh token")
    void shouldRefreshTokenSuccessfully() {
        // Given
        RefreshRequest request = new RefreshRequest("validRefreshToken");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hash")
                .build();

        when(jwtTokenProvider.validateToken("validRefreshToken")).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("validRefreshToken")).thenReturn(1L);
        when(userDataAccess.findById(1L)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("newAccessToken");
        when(responseBuilder.build("newAccessToken"))
                .thenReturn(new RefreshResponse("newAccessToken", 86400000L));

        // When
        RefreshResponse response = useCase.execute(request);

        // Then
        assertThat(response.accessToken()).isEqualTo("newAccessToken");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid token")
    void shouldThrowExceptionForInvalidToken() {
        // Given
        RefreshRequest request = new RefreshRequest("invalidToken");
        when(jwtTokenProvider.validateToken("invalidToken")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid or expired");
    }

    @Test
    @DisplayName("Should throw BadRequestException when access token used as refresh")
    void shouldThrowExceptionForAccessToken() {
        // Given
        RefreshRequest request = new RefreshRequest("accessToken");
        when(jwtTokenProvider.validateToken("accessToken")).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken("accessToken")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not a refresh token");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user not found")
    void shouldThrowExceptionForDeletedUser() {
        // Given
        RefreshRequest request = new RefreshRequest("validRefreshToken");
        when(jwtTokenProvider.validateToken("validRefreshToken")).thenReturn(true);
        when(jwtTokenProvider.isRefreshToken("validRefreshToken")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("validRefreshToken")).thenReturn(999L);
        when(userDataAccess.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("User not found");
    }
}
