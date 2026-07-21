package com.example.chat_app.auth.controller;

import com.example.chat_app.auth.dto.RegisterResponse;
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
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {

        return authService.register(request);


    }

}