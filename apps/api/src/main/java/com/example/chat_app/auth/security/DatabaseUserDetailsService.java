package com.example.chat_app.auth.security;

import com.example.chat_app.users.service.UserLookupService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads users from PostgreSQL for Spring Security.
 *
 * <p>Declaring this bean is what switches the application off Spring Boot's
 * {@code inMemoryUserDetailsManager} fallback: {@code UserDetailsServiceAutoConfiguration} backs
 * off as soon as a {@link UserDetailsService} exists. Until this class existed, no registered user
 * could authenticate at all.
 *
 * <p>It only <em>loads</em> the user. It never compares passwords - that is
 * {@code DaoAuthenticationProvider}'s job, using the BCrypt
 * {@link org.springframework.security.crypto.password.PasswordEncoder}. Keeping the two separate is
 * what allows the encoder to change without touching this class.
 */
@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserLookupService userLookupService;

    public DatabaseUserDetailsService(UserLookupService userLookupService) {
        this.userLookupService = userLookupService;
    }

    /**
     * The interface calls this parameter a username, but this application accepts a username
     * <em>or</em> an email - the login contract ({@code LoginRequest.usernameOrEmail}) allows both.
     *
     * <p>The thrown message is deliberately generic. {@code DaoAuthenticationProvider} additionally
     * converts {@link UsernameNotFoundException} into {@code BadCredentialsException} by default
     * ({@code hideUserNotFoundExceptions = true}), so a caller cannot tell "no such user" from
     * "wrong password" and cannot use the endpoint to enumerate accounts.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userLookupService.findByUsernameOrEmail(usernameOrEmail)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
    }
}
