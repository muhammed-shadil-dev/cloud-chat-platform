package com.example.chat_app.auth.controller;

import jakarta.validation.Valid;
import com.example.chat_app.auth.dto.RegisterRequest;
import com.example.chat_app.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        return "User registered successfully";

    }

}