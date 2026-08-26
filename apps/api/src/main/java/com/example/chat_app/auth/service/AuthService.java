package com.example.chat_app.auth.service;

import com.example.chat_app.auth.dto.RegisterRequest;
import com.example.chat_app.auth.dto.RegisterResponse;
import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.service.UserRegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service //Create one object of this class and manage it.
public class AuthService {

    private final UserRegistrationService userRegistrationService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRegistrationService userRegistrationService, PasswordEncoder passwordEncoder) {
        this.userRegistrationService = userRegistrationService;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        String passwordHash = passwordEncoder.encode(request.getPassword());
        User savedUser = userRegistrationService.register(
                request.getUsername(),
                request.getEmail(),
                passwordHash
        );

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .message("User registered successfully")
                .build();
    }
}
