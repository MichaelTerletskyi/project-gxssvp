package com.gxssvp.dtos;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic API response wrapper containing operation status, message, data, and timestamp.
 *
 * @param <T> type of the response data
 * @author Michael Terletskyi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    /**
     * Creates a successful {@link ApiResponse} with the given message and data.
     *
     * @param message response message
     * @param data    response payload
     * @param <T>     type of the response data
     * @return a success {@link ApiResponse} instance
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a successful {@link ApiResponse} with the given message and data.
     *
     * @param data    response payload
     * @param <T>     type of the response data
     * @return a success {@link ApiResponse} instance
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error {@link ApiResponse} with the given message and data.
     *
     * @param message response message
     * @param data    response payload
     * @param <T>     type of the response data
     * @return a error {@link ApiResponse} instance
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error {@link ApiResponse} with the given message and data.
     *
     * @param message response message
     * @param <T>     type of the response data
     * @return a error {@link ApiResponse} instance
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }
}