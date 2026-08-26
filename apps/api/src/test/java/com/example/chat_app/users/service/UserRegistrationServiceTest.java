package com.example.chat_app.users.service;

import com.example.chat_app.common.exception.EmailAlreadyExistsException;
import com.example.chat_app.common.exception.UsernameAlreadyExistsException;
import com.example.chat_app.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    @Test
    void register_throwsConflictWhenUsernameExists() {
        given(userRepository.existsByUsername("alice")).willReturn(true);

        assertThatThrownBy(() -> userRegistrationService.register(
                "alice", "alice@example.com", "encoded-password"
        )).isInstanceOf(UsernameAlreadyExistsException.class);

        then(userRepository).should().existsByUsername("alice");
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void register_throwsConflictWhenEmailExists() {
        given(userRepository.existsByUsername("alice")).willReturn(false);
        given(userRepository.existsByEmail("alice@example.com")).willReturn(true);

        assertThatThrownBy(() -> userRegistrationService.register(
                "alice", "alice@example.com", "encoded-password"
        )).isInstanceOf(EmailAlreadyExistsException.class);

        then(userRepository).should().existsByUsername("alice");
        then(userRepository).should().existsByEmail("alice@example.com");
        then(userRepository).shouldHaveNoMoreInteractions();
    }
}
