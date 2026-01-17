package com.sathira.miimoneypal.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Spring Security principal representing an authenticated user.
 * Contains user identity and authorities derived from their role.
 */
@Getter
@Builder
public class AppUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Combine role authority with individual permissions
        return Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + role.name())),
                        role.getPermissions().stream()
                                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                )
                .toList();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
