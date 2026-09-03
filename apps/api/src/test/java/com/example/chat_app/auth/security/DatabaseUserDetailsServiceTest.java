package com.example.chat_app.auth.security;

import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.service.UserLookupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DatabaseUserDetailsServiceTest {

    @Mock
    private UserLookupService userLookupService;

    @InjectMocks
    private DatabaseUserDetailsService databaseUserDetailsService;

    private static User user(boolean enabled) {
        return User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("$2a$10$storedhash")
                .enabled(enabled)
                .build();
    }

    @Test
    void loadUserByUsername_returnsDetailsBackedByTheStoredUser() {
        given(userLookupService.findByUsernameOrEmail("alice")).willReturn(Optional.of(user(true)));

        UserDetails details = databaseUserDetailsService.loadUserByUsername("alice");

        assertThat(details).isInstanceOf(CustomUserDetails.class);
        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("$2a$10$storedhash");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).isEmpty();
    }

    /** The login contract accepts a username or an email, so the identifier passes through as-is. */
    @Test
    void loadUserByUsername_acceptsAnEmailAsTheIdentifier() {
        given(userLookupService.findByUsernameOrEmail("alice@example.com"))
                .willReturn(Optional.of(user(true)));

        UserDetails details = databaseUserDetailsService.loadUserByUsername("alice@example.com");

        assertThat(details.getUsername()).isEqualTo("alice");
    }

    @Test
    void loadUserByUsername_throwsWhenNoUserMatches() {
        given(userLookupService.findByUsernameOrEmail("ghost")).willReturn(Optional.empty());

        assertThatThrownBy(() -> databaseUserDetailsService.loadUserByUsername("ghost"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid credentials");
    }

    /**
     * A disabled account still loads - rejecting it is the AuthenticationProvider's job, not this
     * class's. Surfacing the flag correctly is what lets that check work.
     */
    @Test
    void loadUserByUsername_reportsDisabledAccountsAsDisabled() {
        given(userLookupService.findByUsernameOrEmail("alice")).willReturn(Optional.of(user(false)));

        assertThat(databaseUserDetailsService.loadUserByUsername("alice").isEnabled()).isFalse();
    }
}
