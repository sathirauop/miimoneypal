package com.sathira.miimoneypal.rest.auth.register;

import com.sathira.miimoneypal.records.user.User;

/**
 * Interface for building registration response.
 * Transforms domain User and tokens into response DTO.
 */
public interface RegisterResponseBuilder {
    RegisterResponse build(User user, String accessToken, String refreshToken);
}
