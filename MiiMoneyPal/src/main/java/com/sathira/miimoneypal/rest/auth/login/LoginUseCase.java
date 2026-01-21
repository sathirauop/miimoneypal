package com.sathira.miimoneypal.rest.auth.login;

import com.sathira.miimoneypal.architecture.UseCase;
import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import com.sathira.miimoneypal.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Use case for user login.
 *
 * Business Rules:
 * 1. Validate credentials via Spring Security AuthenticationManager
 * 2. Generate both access and refresh tokens
 * 3. Return user info for frontend state
 *
 * Error Handling:
 * - BadCredentialsException thrown by AuthenticationManager for invalid credentials
 * - Handled by GlobalExceptionHandler -> 401 Unauthorized
 */
@Service
@RequiredArgsConstructor
public class LoginUseCase implements UseCase<LoginRequest, LoginResponse> {

    private final AuthenticationManager authenticationManager;
    private final UserDataAccess userDataAccess;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginResponseBuilder responseBuilder;

    @Override
    public LoginResponse execute(LoginRequest request) {
        // 1. Authenticate via Spring Security (throws BadCredentialsException if invalid)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().toLowerCase().trim(),
                        request.password()
                )
        );

        // 2. Get authenticated user
        AppUser appUser = (AppUser) authentication.getPrincipal();

        // 3. Fetch full user record for response (includes currencySymbol)
        User user = userDataAccess.findById(appUser.getId())
                .orElseThrow(); // Should never happen after successful auth

        // 4. Generate JWT tokens
        String accessToken = jwtTokenProvider.generateAccessToken(appUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(appUser);

        // 5. Build and return response
        return responseBuilder.build(user, accessToken, refreshToken);
    }
}
