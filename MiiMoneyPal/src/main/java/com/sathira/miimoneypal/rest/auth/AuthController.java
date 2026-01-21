package com.sathira.miimoneypal.rest.auth;

import com.sathira.miimoneypal.constants.EndPoints;
import com.sathira.miimoneypal.rest.auth.login.LoginRequest;
import com.sathira.miimoneypal.rest.auth.login.LoginResponse;
import com.sathira.miimoneypal.rest.auth.login.LoginUseCase;
import com.sathira.miimoneypal.rest.auth.refresh.RefreshRequest;
import com.sathira.miimoneypal.rest.auth.refresh.RefreshResponse;
import com.sathira.miimoneypal.rest.auth.refresh.RefreshUseCase;
import com.sathira.miimoneypal.rest.auth.register.RegisterRequest;
import com.sathira.miimoneypal.rest.auth.register.RegisterResponse;
import com.sathira.miimoneypal.rest.auth.register.RegisterUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 * All endpoints are public (configured in SecurityConfig).
 *
 * Endpoints:
 * - POST /api/auth/register - Create new user account
 * - POST /api/auth/login    - Authenticate and get tokens
 * - POST /api/auth/refresh  - Refresh access token
 */
@RestController
@RequestMapping(EndPoints.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;

    /**
     * Register a new user account.
     *
     * @param request Registration details (email, password, optional currency)
     * @return User info and JWT tokens for immediate login
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = registerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and return JWT tokens.
     *
     * @param request Login credentials (email, password)
     * @return User info and JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = loginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param request Refresh token
     * @return New access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshResponse response = refreshUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
