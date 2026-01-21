package com.sathira.miimoneypal.rest.auth.refresh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Presenter for refresh response.
 */
@Component
public class RefreshPresenter implements RefreshResponseBuilder {

    private final long accessTokenExpiration;

    public RefreshPresenter(
            @Value("${jwt.access-token-expiration:86400000}") long accessTokenExpiration
    ) {
        this.accessTokenExpiration = accessTokenExpiration;
    }

    @Override
    public RefreshResponse build(String accessToken) {
        return new RefreshResponse(accessToken, accessTokenExpiration);
    }
}
