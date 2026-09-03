package com.example.chat_app.users.service;

import com.example.chat_app.users.entity.User;
import com.example.chat_app.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Read-side access to users, owned by the {@code users} module.
 *
 * <p>This exists so other modules never reach into {@link UserRepository} directly. The
 * {@code auth} module needs to load a user to authenticate them, and going through this service
 * keeps that dependency on behavior rather than on the persistence model.
 */
@Service
public class UserLookupService {

    private final UserRepository userRepository;

    public UserLookupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Finds a user by username, falling back to email.
     *
     * <p>Username is tried first because it is the more common login identifier. Both columns are
     * uniquely constrained, so at most one user can match either lookup.
     *
     * @return the user, or empty if neither a username nor an email matches
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail));
    }
}
