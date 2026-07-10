package com.example.chat_app.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//creating password encoder bean
@Configuration  //tells spring:this class provides application configuration
public class PasswordConfig {
    @Bean  //create one PasswordEncoder object and save it in the spring container
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}