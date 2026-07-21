package com.gxssvp.exceptions;

import java.util.HashMap;
import java.util.Map;
import com.gxssvp.dtos.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Security module exception handler for error handling across all its controllers.
 * Converts exceptions into standardized {@link ApiResponse} objects with appropriate HTTP status codes.
 *
 * @author Michael Terletskyi
 */
@Log4j2
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SecurityExceptionHandler {

    /**
     * Handles user Enlist exceptions .
     */
    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUserRegistrationException(
            UserRegistrationException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Registration failed", ex.getData()));
    }

    /**
     * Handles user login exceptions .
     */
    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleUserLoginException(
            UserLoginException ex) {
        log.error(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Login failed", ex.getData()));
    }

    /**
     * Handles general exceptions .
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                {
                    final String field = fieldError.getField();
                    errors.put(field, fieldError.getDefaultMessage());
                }
        );

        log.warn("Validation failed: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Validation failed", errors));
    }
}