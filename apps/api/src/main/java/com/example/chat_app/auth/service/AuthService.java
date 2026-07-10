package com.example.chat_app.auth.service;

import com.example.chat_app.auth.dto.RegisterRequest;
import com.example.chat_app.auth.entity.User;
import com.example.chat_app.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service //Create one object of this class and manage it.
public class AuthService {

    private final UserRepository userRepository; //Constructor Injection:This service depends on UserRepository.
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder) {//Spring says:"I already have a UserRepository object."
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        System.out.println("AuthService Created Successfully!");

    }
    public void register(RegisterRequest request) {
       // Check if Username Already Exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
    }
}