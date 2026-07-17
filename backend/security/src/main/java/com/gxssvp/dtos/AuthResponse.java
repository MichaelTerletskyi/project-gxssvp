package com.gxssvp.dtos;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for authentication requests.
 * Contains the access and refresh tokens, user information, and token type.
 *
 * @author Michael Terletskyi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private final String type = "Bearer";
    private UUID id;
    private String username;
    private String email;
    private String role;
}