package controllers;

import com.gxssvp.config.SecurityConfig;
import com.gxssvp.config.TestSecurityConfig;
import com.gxssvp.controllers.AuthController;
import com.gxssvp.dtos.*;
import com.gxssvp.jwt.JwtAuthenticationFilter;
import com.gxssvp.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {
        AuthController.class,
        TestSecurityConfig.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private final String BASE_URL = "/rest/api/v1/auth";

    @Nested
    @DisplayName("POST /register")
    class RegisterTests {

        @Test
        @DisplayName("Should return 200 OK with success payload when request is valid")
        void shouldRegisterSuccessfully() throws Exception {
            final RegisterRequest request = new RegisterRequest();
            request.setUsername("john_doe");
            request.setEmail("john@example.com");
            request.setPassword("password1231@#$");

            final AuthResponse authResponse = AuthResponse.builder()
                    .id(UUID.randomUUID())
                    .username("john_doe")
                    .email("john@example.com")
                    .role("USER")
                    .accessToken("mock-access-token")
                    .refreshToken("mock-refresh-token")
                    .build();

            given(authService.register(any(RegisterRequest.class))).willReturn(authResponse);

            mockMvc.perform(post(BASE_URL + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully"))
                    .andExpect(jsonPath("$.data.username").value("john_doe"))
                    .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"));

            verify(authService).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body violates validation (@Valid)")
        void shouldReturn400WhenInvalidRequest() throws Exception {
            final RegisterRequest invalidRequest = new RegisterRequest();
            invalidRequest.setUsername("");
            invalidRequest.setEmail("invalid-email");
            invalidRequest.setPassword("");

            mockMvc.perform(post(BASE_URL + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /login")
    class LoginTests {

        @Test
        @DisplayName("Should return 200 OK on valid credentials")
        void shouldLoginSuccessfully() throws Exception {
            final LoginRequest request = new LoginRequest();
            request.setUsername("john_doe");
            request.setPassword("password1231@#$");

            final AuthResponse authResponse = AuthResponse.builder()
                    .username("john_doe")
                    .accessToken("mock-access-token")
                    .build();

            given(authService.login(any(LoginRequest.class))).willReturn(authResponse);

            mockMvc.perform(post(BASE_URL + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));
        }
    }

    @Nested
    @DisplayName("POST /refresh")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should return 200 OK with new tokens")
        void shouldRefreshTokenSuccessfully() throws Exception {
            final RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken("old-refresh-token");

            final RefreshTokenResponse response = RefreshTokenResponse.builder()
                    .accessToken("new-access-token")
                    .refreshToken("new-refresh-token")
                    .build();

            given(authService.refreshToken(any(RefreshTokenRequest.class))).willReturn(response);

            mockMvc.perform(post(BASE_URL + "/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Token refreshed successfully"))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when refreshToken is blank")
        void shouldFailValidationWhenRefreshTokenIsBlank() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken("");

            mockMvc.perform(post(BASE_URL + "/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /logout")
    class LogoutTests {

        @Test
        @DisplayName("Should return 200 OK on successful logout")
        void shouldLogoutSuccessfully() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken("some-refresh-token");

            doNothing().when(authService).logout("some-refresh-token");

            mockMvc.perform(post(BASE_URL + "/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logged out successfully"))
                    .andExpect(jsonPath("$.data").doesNotExist());

            verify(authService).logout("some-refresh-token");
        }
    }
}