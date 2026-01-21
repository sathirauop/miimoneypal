package com.sathira.miimoneypal.rest.auth.login;

import com.sathira.miimoneypal.records.user.User;

/**
 * Interface for building login response.
 */
public interface LoginResponseBuilder {
    LoginResponse build(User user, String accessToken, String refreshToken);
}
