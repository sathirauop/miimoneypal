package com.sathira.miimoneypal.rest.auth.register;

import com.sathira.miimoneypal.architecture.UseCase;
import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import com.sathira.miimoneypal.security.Role;
import com.sathira.miimoneypal.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for user registration.
 *
 * Business Rules:
 * 1. Email must be unique (case-insensitive)
 * 2. Password is hashed with BCrypt before storage
 * 3. Returns JWT tokens for immediate login after registration
 * 4. Default currency is LKR if not specified
 */
@Service
@RequiredArgsConstructor
public class RegisterUseCase implements UseCase<RegisterRequest, RegisterResponse> {

    private final UserDataAccess userDataAccess;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegisterResponseBuilder responseBuilder;

    @Override
    @Transactional
    public RegisterResponse execute(RegisterRequest request) {
        // 1. Hash password with BCrypt
        String passwordHash = passwordEncoder.encode(request.password());

        // 2. Create user domain object
        User newUser = User.builder()
                .email(request.email().toLowerCase().trim())
                .passwordHash(passwordHash)
                .currencySymbol(request.currencySymbol())  // null defaults to LKR in User record
                .build();

        // 3. Save user (throws DuplicateResourceException if email exists)
        User savedUser = userDataAccess.save(newUser);

        // 4. Generate JWT tokens for immediate login
        AppUser appUser = AppUser.builder()
                .id(savedUser.id())
                .email(savedUser.email())
                .passwordHash(savedUser.passwordHash())
                .role(Role.USER)
                .build();

        String accessToken = jwtTokenProvider.generateAccessToken(appUser);
        String refreshToken = jwtTokenProvider.generateRefreshToken(appUser);

        // 5. Build and return response
        return responseBuilder.build(savedUser, accessToken, refreshToken);
    }
}
