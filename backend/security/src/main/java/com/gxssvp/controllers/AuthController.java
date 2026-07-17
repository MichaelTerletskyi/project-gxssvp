package com.gxssvp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.gxssvp.dtos.ApiResponse;
import com.gxssvp.dtos.AuthResponse;
import com.gxssvp.dtos.LoginRequest;
import com.gxssvp.dtos.RefreshTokenRequest;
import com.gxssvp.dtos.RefreshTokenResponse;
import com.gxssvp.dtos.RegisterRequest;
import com.gxssvp.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication and authorization operations.
 * Provides endpoints for login, registration, token refresh, and logout actions.
 *
 * @author Michael Terletskyi
 */
@Log4j2
@RestController
@RequestMapping("/rest/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Registers a new user account.
     *
     * @param request registration payload containing username, email, and password
     * @return API response containing generated access and refresh tokens
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Attempt to register user '{}'", request.getUsername());
        final AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    /**
     * Authenticates a user and issues new access and refresh tokens.
     *
     * @param request login payload containing username and password
     * @return API response containing authenticated user info and tokens
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Attempt to login user '{}'", request.getUsername());
        final AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * Issues a new access token and refresh token pair.
     *
     * @param request payload containing an existing refresh token
     * @return API response containing newly generated tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        log.info("Attempt to refresh token {}", request.getRefreshToken());
        final RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    /**
     * Revokes an existing refresh token and logs the user out.
     *
     * @param request payload containing the refresh token to revoke
     * @return API success response with no body content
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Attempt to logout by token {}", request.getRefreshToken());
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}