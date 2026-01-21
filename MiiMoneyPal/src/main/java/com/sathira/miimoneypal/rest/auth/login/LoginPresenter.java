package com.sathira.miimoneypal.rest.auth.login;

import com.sathira.miimoneypal.records.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Presenter for login response.
 */
@Component
public class LoginPresenter implements LoginResponseBuilder {

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public LoginPresenter(
            @Value("${jwt.access-token-expiration:86400000}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshTokenExpiration
    ) {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Override
    public LoginResponse build(User user, String accessToken, String refreshToken) {
        return new LoginResponse(
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
