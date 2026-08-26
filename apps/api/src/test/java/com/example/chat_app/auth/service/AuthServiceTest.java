package com.example.chat_app.auth.service;

import com.example.chat_app.auth.dto.RegisterRequest;
import com.example.chat_app.auth.dto.RegisterResponse;
import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_hashesPasswordAndReturnsRegisteredUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        User savedUser = User.builder()
                .id(1L)
                .username("alice")
                .email("alice@example.com")
                .password("encoded-password")
                .build();

        given(passwordEncoder.encode("password123")).willReturn("encoded-password");
        given(userRegistrationService.register("alice", "alice@example.com", "encoded-password"))
                .willReturn(savedUser);

        RegisterResponse response = authService.register(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
        assertThat(response.getMessage()).isEqualTo("User registered successfully");
        then(passwordEncoder).should().encode("password123");
        then(userRegistrationService).should().register("alice", "alice@example.com", "encoded-password");
    }
}
