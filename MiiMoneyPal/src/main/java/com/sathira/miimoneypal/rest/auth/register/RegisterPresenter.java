package com.sathira.miimoneypal.rest.auth.register;

import com.sathira.miimoneypal.records.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Presenter for registration response.
 * Transforms domain User and tokens into RegisterResponse DTO.
 */
@Component
public class RegisterPresenter implements RegisterResponseBuilder {

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public RegisterPresenter(
            @Value("${jwt.access-token-expiration:86400000}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration
    ) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public RegisterResponse build(User user, String accessToken, String refreshToken) {
        return new RegisterResponse(
                user.id(),
                user.email(),
                user.currencySymbol(),
                accessToken,
                refreshToken,
                accessTokenExpiration,
                refreshTokenExpiration
        );
    }
}
