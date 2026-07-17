package com.gxssvp.exceptions;

/**
 * Exception thrown when a refresh token operation failed.
 *
 * @author Michael Terletskyi
 */
public class RefreshTokenException extends RuntimeException {
    public RefreshTokenException(String message) {
        super(message);
    }
}