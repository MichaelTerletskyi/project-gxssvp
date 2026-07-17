package com.gxssvp.exceptions;

import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;


/**
 * Exception thrown when user login failed.
 *
 * @author Michael Terletskyi
 */
@Getter
public class UserLoginException extends AuthenticationException {
    private final Map<String, String> data;

    public UserLoginException(String message) {
        super(message);
        this.data = null;
    }

    public UserLoginException(String message, Map<String, String> data) {
        super(message);
        this.data = data;
    }
}