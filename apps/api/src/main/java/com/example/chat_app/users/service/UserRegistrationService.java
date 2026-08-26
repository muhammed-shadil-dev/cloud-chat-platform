package com.example.chat_app.users.service;

import com.example.chat_app.common.exception.EmailAlreadyExistsException;
import com.example.chat_app.common.exception.UsernameAlreadyExistsException;
import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(String username, String email, String passwordHash) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException();
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordHash)
                .build();

        return userRepository.save(user);
    }
}
