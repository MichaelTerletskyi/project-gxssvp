package com.gxssvp.services;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import com.gxssvp.entities.RefreshToken;
import com.gxssvp.entities.User;
import com.gxssvp.repositories.RefreshTokenRepository;
import com.gxssvp.repositories.UserRepository;
import com.gxssvp.config.JwtProperties;
import com.gxssvp.exceptions.RefreshTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for managing refresh tokens, including creation, verification,
 * revocation, and cleanup of expired tokens.
 *
 * @author Michael Terletskyi
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    /**
     * Creates a new refresh token for the specified username.
     * If a previous token exists, it is deleted.
     *
     * @param username the username for which to create a new token
     * @return the newly created {@link RefreshToken}
     * @throws RefreshTokenException if the user is not found
     */
    public RefreshToken createRefreshToken(String username) {
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RefreshTokenException("User not found"));

        refreshTokenRepository.deleteByUser(user);

        final RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verifies the validity of a refresh token.
     * Deletes and rejects expired tokens or revoked ones.
     *
     * @param token the refresh token string
     * @return the valid {@link RefreshToken}
     * @throws RefreshTokenException if the token is not found, expired, or revoked
     */

    public RefreshToken verifyRefreshToken(String token) {
        final RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    /**
     * Marks the specified refresh token as revoked.
     *
     * @param token the token string to revoke
     * @throws RefreshTokenException if the token does not exist
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        final RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Deletes all refresh tokens associated with the specified user.
     *
     * @param username the username whose tokens will be removed
     * @throws RefreshTokenException if the user is not found
     */
    @Transactional
    public void revokeAllUserTokens(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RefreshTokenException("User not found"));

        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens();
    }
}