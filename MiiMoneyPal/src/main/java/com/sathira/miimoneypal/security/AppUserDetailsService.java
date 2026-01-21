package com.sathira.miimoneypal.security;

import com.sathira.miimoneypal.records.user.User;
import com.sathira.miimoneypal.repository.UserDataAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security UserDetailsService implementation for loading users from database.
 * Used during login authentication to validate credentials.
 *
 * <p>NOTE: This is NOT used for JWT token validation (JwtAuthenticationFilter handles that).
 * This service is only invoked during initial login via AuthenticationManager.</p>
 *
 * <p>Integration Points:</p>
 * <ul>
 *   <li>Login endpoint: AuthenticationManager calls this to load user credentials</li>
 *   <li>Password verification: DaoAuthenticationProvider compares passwords using BCrypt</li>
 *   <li>JWT generation: After successful auth, JwtTokenProvider creates tokens</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserDataAccess userDataAccess;

    /**
     * Load user by email (username) for Spring Security authentication.
     *
     * <p>This method is called by Spring Security's AuthenticationManager during login.
     * It loads the user from the database and converts it to Spring Security's UserDetails.</p>
     *
     * @param email the user's email address (used as username in login form)
     * @return UserDetails (AppUser) containing credentials and authorities
     * @throws UsernameNotFoundException if user not found in database
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDataAccess.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        return toAppUser(user);
    }

    /**
     * Convert domain User record to Spring Security AppUser.
     *
     * <p>All users are assigned the USER role by default.
     * Future enhancement: Read role from database if admin functionality is added.</p>
     *
     * @param user the domain user record from database
     * @return AppUser with all required Spring Security fields populated
     */
    private AppUser toAppUser(User user) {
        return AppUser.builder()
                .id(user.id())
                .email(user.email())
                .passwordHash(user.passwordHash())
                .role(Role.USER)  // Default role for all users
                .build();
    }
}
