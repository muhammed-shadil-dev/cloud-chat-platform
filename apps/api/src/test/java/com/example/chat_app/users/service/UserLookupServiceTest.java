package com.example.chat_app.users.service;

import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserLookupServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserLookupService userLookupService;

    private static User user() {
        return User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("$2a$10$hash")
                .build();
    }

    @Test
    void findByUsernameOrEmail_findsUserByUsername() {
        given(userRepository.findByUsername("alice")).willReturn(Optional.of(user()));

        Optional<User> found = userLookupService.findByUsernameOrEmail("alice");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("alice");
    }

    /** The email lookup must not run when the username already matched - it would be a wasted query. */
    @Test
    void findByUsernameOrEmail_doesNotQueryByEmailWhenUsernameMatches() {
        given(userRepository.findByUsername("alice")).willReturn(Optional.of(user()));

        userLookupService.findByUsernameOrEmail("alice");

        then(userRepository).should(never()).findByEmail("alice");
    }

    @Test
    void findByUsernameOrEmail_fallsBackToEmail() {
        given(userRepository.findByUsername("alice@example.com")).willReturn(Optional.empty());
        given(userRepository.findByEmail("alice@example.com")).willReturn(Optional.of(user()));

        Optional<User> found = userLookupService.findByUsernameOrEmail("alice@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findByUsernameOrEmail_returnsEmptyWhenNeitherMatches() {
        given(userRepository.findByUsername("ghost")).willReturn(Optional.empty());
        given(userRepository.findByEmail("ghost")).willReturn(Optional.empty());

        assertThat(userLookupService.findByUsernameOrEmail("ghost")).isEmpty();
    }
}
