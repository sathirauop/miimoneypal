package com.sathira.miimoneypal.rest.auth.refresh;

/**
 * Interface for building refresh response.
 */
public interface RefreshResponseBuilder {
    RefreshResponse build(String accessToken);
}
