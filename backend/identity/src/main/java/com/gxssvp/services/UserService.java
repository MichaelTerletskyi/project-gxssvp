package com.gxssvp.services;

import java.util.UUID;
import com.gxssvp.dtos.UserResponse;
import com.gxssvp.entities.User;
import com.gxssvp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for operations with {@link User}.
 *
 * @author Michael Terletskyi
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Returns a user by ID as {@link UserResponse}.
     */
    public UserResponse getUserById(UUID id) {
        final User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    /**
     * Returns a user by username as {@link UserResponse}.
     */
    public UserResponse getUserByUsername(String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    /**
     * Maps a {@link User} entity to {@link UserResponse} DTO.
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .build();
    }
}