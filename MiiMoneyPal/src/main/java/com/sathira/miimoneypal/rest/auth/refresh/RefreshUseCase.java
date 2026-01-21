package com.sathira.miimoneypal.rest.auth.refresh;

import com.sathira.miimoneypal.architecture.UseCase;
import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import com.sathira.miimoneypal.security.Role;
import com.sathira.miimoneypal.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * Use case for refreshing access token.
 *
 * Business Rules:
 * 1. Refresh token must be valid (not expired, correct signature)
 * 2. Token must be of type "refresh" (not an access token)
 * 3. User must still exist in the database
 * 4. Returns new access token (refresh token is NOT rotated)
 *
 * Security Note: Refresh token rotation (returning new refresh token each time)
 * is a V2 enhancement for better security. MVP uses simpler approach.
 */
@Service
@RequiredArgsConstructor
public class RefreshUseCase implements UseCase<RefreshRequest, RefreshResponse> {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDataAccess userDataAccess;
    private final RefreshResponseBuilder responseBuilder;

    @Override
    public RefreshResponse execute(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        // 1. Validate token signature and expiration
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        // 2. Verify it's a refresh token (not an access token)
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new BadRequestException("Token is not a refresh token");
        }

        // 3. Extract user ID and verify user still exists
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userDataAccess.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // 4. Generate new access token
        AppUser appUser = AppUser.builder()
                .id(user.id())
                .email(user.email())
                .passwordHash(user.passwordHash())
                .role(Role.USER)
                .build();

        String newAccessToken = jwtTokenProvider.generateAccessToken(appUser);

        // 5. Build and return response
        return responseBuilder.build(newAccessToken);
    }
}
