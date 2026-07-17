package com.gxssvp.exceptions;

import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * Exception thrown when user registration failed.
 *
 * @author Michael Terletskyi
 */
@Getter
public class UserRegistrationException extends AuthenticationException {
    private final Map<String, String>  data;

    public UserRegistrationException(String message) {
        super(message);
        this.data = null;
    }

    public UserRegistrationException(String message, Map<String, String> data) {
        super(message);
        this.data = data;
    }
}