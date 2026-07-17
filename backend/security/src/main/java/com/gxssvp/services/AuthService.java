package com.gxssvp.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.gxssvp.entities.RefreshToken;
import com.gxssvp.entities.Role;
import com.gxssvp.entities.User;
import com.gxssvp.repositories.UserRepository;
import com.gxssvp.dtos.AuthResponse;
import com.gxssvp.dtos.LoginRequest;
import com.gxssvp.dtos.RefreshTokenRequest;
import com.gxssvp.dtos.RefreshTokenResponse;
import com.gxssvp.dtos.RegisterRequest;
import com.gxssvp.exceptions.UserLoginException;
import com.gxssvp.exceptions.UserRegistrationException;
import com.gxssvp.jwt.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * Service handling authentication, registration, and token lifecycle operations.
 * Manages access and refresh token generation, validation, and revocation.
 *
 * @author Michael Terletskyi
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Validated
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final Validator validator;

    /**
     * Registers a new user and returns authentication tokens.
     */
    @Transactional
    public AuthResponse register(final RegisterRequest request) {
        final User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .createdAt(Instant.now())
                .build();

        checkForViolations(user);

        final User savedUser = userRepository.save(user);
        log.info("User saved: id='{}', username='{}'", savedUser.getId(), savedUser.getUsername());

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        log.info("Access token generated for username='{}'", request.getUsername());

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    /**
     * Detailed check-in on errors of a User instance.
     */
    private void checkForViolations(final User user) {
        final Map<String, String> errors = new HashMap<>();
        if (userRepository.existsByUsername(user.getUsername())) {
            errors.put("username", String.format("Username '%s' is already taken", user.getUsername()));
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            errors.put("email", String.format("Email '%s' is already taken", user.getEmail()));
        }

        final Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> {
                final String fieldName = violation.getPropertyPath().toString();
                final String errorMessage = violation.getMessage();
                errors.put(fieldName, errorMessage);
            });
        }

        if (!errors.isEmpty()) {
            log.warn("User registration failed: {}", errors);
            throw new UserRegistrationException("Invalid user data", errors);
        }
    }

    /**
     * Authenticates an existing user and issues new tokens.
     */
    @Transactional
    public AuthResponse login(final LoginRequest request) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getUsername());
        log.info("Refresh token generated for username='{}'", request.getUsername());

        final User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserLoginException("User not found"));

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Generates a new access and refresh token pair using an existing valid refresh token.
     */

    @Transactional
    public RefreshTokenResponse refreshToken(final RefreshTokenRequest request) {
        final RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        log.info("Refresh token verified for username='{}'", refreshToken.getUser().getUsername());

        final String username = refreshToken.getUser().getUsername();
        final String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        final RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(username);
        log.info("New refresh token generated for username='{}'", username);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        log.info("Logout request received - revoking refresh token");
        refreshTokenService.revokeRefreshToken(refreshToken);
    }
}