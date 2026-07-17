package com.gxssvp.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request DTO for refreshing JWT access tokens.
 * Contains the refresh token provided by the client to obtain a new access token.
 *
 * @author Michael Terletskyi
 */
@Data
public class RefreshTokenRequest {

    /**
     * The refresh token used to generate a new access token.
     */
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}