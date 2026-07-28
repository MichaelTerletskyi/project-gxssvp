package services;

import com.gxssvp.dtos.*;
import com.gxssvp.exceptions.UserLoginException;
import com.gxssvp.exceptions.UserRegistrationException;
import com.gxssvp.services.AuthService;
import com.gxssvp.services.RefreshTokenService;
import com.gxssvp.entities.RefreshToken;
import com.gxssvp.entities.Role;
import com.gxssvp.entities.User;
import com.gxssvp.repositories.UserRepository;
import com.gxssvp.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

/**
 * Test of Service handling authentication, registration, and token lifecycle operations.
 * It checks how Manages access and refresh token generation, validation, and revocation.
 *
 * @author Michael Terletskyi
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @Nested
    @DisplayName("register()")
    class Register {

        @Test
        @DisplayName("Should successfully register a new user and return AuthResponse")
        void shouldRegisterUserSuccessfully() {
            final RegisterRequest request = new RegisterRequest();
            request.setUsername("john_doe");
            request.setEmail("john@example.com");
            request.setPassword( "rawPassword123");

            given(userRepository.existsByUsername(request.getUsername())).willReturn(false);
            given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
            given(passwordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

            final UUID generatedId = UUID.randomUUID();
            final User savedUser = User.builder()
                    .id(generatedId)
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .passwordHash("encodedPassword")
                    .role(Role.USER)
                    .enabled(true)
                    .build();

            given(userRepository.save(any(User.class))).willReturn(savedUser);
            given(jwtTokenProvider.generateAccessToken(any(Authentication.class))).willReturn("mock-access-token");

            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken("mock-refresh-token");
            given(refreshTokenService.createRefreshToken(request.getUsername())).willReturn(refreshToken);

            final AuthResponse response = authService.register(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(generatedId);
            assertThat(response.getUsername()).isEqualTo("john_doe");
            assertThat(response.getEmail()).isEqualTo("john@example.com");
            assertThat(response.getRole()).isEqualTo("USER");
            assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token");

            final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User capturedUser = userCaptor.getValue();
            assertThat(capturedUser.getPasswordHash()).isEqualTo("encodedPassword");
        }

        @Test
        @DisplayName("Should throw UserRegistrationException when username is already taken")
        void shouldThrowExceptionWhenUsernameIsTaken() {
            final RegisterRequest request = new RegisterRequest();
            request.setUsername("john_doe");
            request.setEmail("john@example.com");
            request.setPassword( "rawPassword123");

            given(userRepository.existsByUsername(request.getUsername())).willReturn(true);
            given(userRepository.existsByEmail(request.getEmail())).willReturn(false);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(UserRegistrationException.class)
                    .hasMessage("Invalid user data")
                    .satisfies(ex -> {
                        UserRegistrationException registrationException = (UserRegistrationException) ex;
                        assertThat(registrationException.getData())
                                .containsEntry("username", "Username 'john_doe' is already taken");
                    });

            verify(userRepository, never()).save(any());
            verify(jwtTokenProvider, never()).generateAccessToken((Authentication) any());
        }

        @Test
        @DisplayName("Should throw UserRegistrationException when email is already taken")
        void shouldThrowExceptionWhenEmailIsTaken() {
            final RegisterRequest request = new RegisterRequest();
            request.setUsername("john_doe");
            request.setEmail("john@example.com");
            request.setPassword( "rawPassword123");

            given(userRepository.existsByUsername(request.getUsername())).willReturn(false);
            given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(UserRegistrationException.class)
                    .hasMessage("Invalid user data");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should capture both username and email errors when both are taken")
        void shouldCollectBothErrorsWhenUsernameAndEmailAreTaken() {
            final RegisterRequest request = new RegisterRequest();
            request.setUsername("john_doe");
            request.setEmail("john@example.com");
            request.setPassword( "rawPassword123");

            given(userRepository.existsByUsername(request.getUsername())).willReturn(true);
            given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(UserRegistrationException.class)
                    .hasFieldOrPropertyWithValue("message", "Invalid user data");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("Should successfully authenticate user and return AuthResponse")
        void shouldLoginUserSuccessfully() {
            final LoginRequest request = new LoginRequest();
            request.setUsername("john_doe");
            request.setPassword("rawPassword123");

            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()
            );

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(authentication);
            given(jwtTokenProvider.generateAccessToken(authentication))
                    .willReturn("mock-access-token");

            final RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken("mock-refresh-token");
            given(refreshTokenService.createRefreshToken(request.getUsername()))
                    .willReturn(refreshToken);

            UUID userId = UUID.randomUUID();
            User user = User.builder()
                    .id(userId)
                    .username(request.getUsername())
                    .email("john@example.com")
                    .role(Role.USER)
                    .build();
            given(userRepository.findByUsername(request.getUsername()))
                    .willReturn(Optional.of(user));

            final AuthResponse response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(userId);
            assertThat(response.getUsername()).isEqualTo("john_doe");
            assertThat(response.getEmail()).isEqualTo("john@example.com");
            assertThat(response.getRole()).isEqualTo("USER");
            assertThat(response.getAccessToken()).isEqualTo("mock-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("mock-refresh-token");

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenProvider).generateAccessToken(authentication);
            verify(refreshTokenService).createRefreshToken(request.getUsername());
            verify(userRepository).findByUsername(request.getUsername());
        }

        @Test
        @DisplayName("Should propagate exception when AuthenticationManager fails (e.g. BadCredentialsException)")
        void shouldThrowExceptionWhenCredentialsAreInvalid() {
            final LoginRequest request = new LoginRequest();
            request.setUsername("john_doe");
            request.setPassword("rawPassword123");

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Bad credentials");

            verify(jwtTokenProvider, never()).generateAccessToken((Authentication) any());
            verify(refreshTokenService, never()).createRefreshToken(any());
            verify(userRepository, never()).findByUsername(any());
        }

        @Test
        @DisplayName("Should throw UserLoginException when authenticated user is not found in repository")
        void shouldThrowUserLoginExceptionWhenUserNotFound() {
            final LoginRequest request = new LoginRequest();
            request.setUsername("john_doe");
            request.setPassword("rawPassword123");

            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()
            );

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(authentication);
            given(jwtTokenProvider.generateAccessToken(authentication))
                    .willReturn("mock-access-token");

            final RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken("mock-refresh-token");
            given(refreshTokenService.createRefreshToken(request.getUsername()))
                    .willReturn(refreshToken);

            given(userRepository.findByUsername(request.getUsername()))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UserLoginException.class)
                    .hasMessage("User not found");
        }
    }

    @Nested
    @DisplayName("refreshToken()")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should successfully verify refresh token and return new tokens")
        void shouldRefreshTokenSuccessfully() {
            final String oldTokenString = "valid-old-refresh-token";
            final RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken(oldTokenString);

            final User mockUser = User.builder()
                    .id(UUID.randomUUID())
                    .username("john_doe")
                    .build();

            final RefreshToken existingRefreshToken = new RefreshToken();
            existingRefreshToken.setToken(oldTokenString);
            existingRefreshToken.setUser(mockUser);

            given(refreshTokenService.verifyRefreshToken(oldTokenString))
                    .willReturn(existingRefreshToken);

            given(jwtTokenProvider.generateAccessToken("john_doe"))
                    .willReturn("new-access-token");

            final RefreshToken newlyCreatedRefreshToken = new RefreshToken();
            newlyCreatedRefreshToken.setToken("new-refresh-token");
            given(refreshTokenService.createRefreshToken("john_doe"))
                    .willReturn(newlyCreatedRefreshToken);

            final RefreshTokenResponse response = authService.refreshToken(request);

            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");

            verify(refreshTokenService).verifyRefreshToken(oldTokenString);
            verify(jwtTokenProvider).generateAccessToken("john_doe");
            verify(refreshTokenService).createRefreshToken("john_doe");
        }

        @Test
        @DisplayName("Should propagate exception when refresh token verification fails")
        void shouldThrowExceptionWhenRefreshTokenIsInvalidOrExpired() {
            final String invalidToken = "invalid-or-expired-token";
            final RefreshTokenRequest request = new RefreshTokenRequest();
            request.setRefreshToken(invalidToken);

            given(refreshTokenService.verifyRefreshToken(invalidToken))
                    .willThrow(new RuntimeException("Refresh token was expired or invalid"));

            assertThatThrownBy(() -> authService.refreshToken(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Refresh token was expired or invalid");

            verify(jwtTokenProvider, never()).generateAccessToken(anyString());
            verify(refreshTokenService, never()).createRefreshToken(anyString());
        }
    }

    @Nested
    @DisplayName("logout()")
    class LogoutTests {

        @Test
        @DisplayName("Should successfully revoke refresh token")
        void shouldRevokeRefreshTokenSuccessfully() {
            String refreshToken = "valid-refresh-token";

            authService.logout(refreshToken);

            verify(refreshTokenService).revokeRefreshToken(refreshToken);
        }

        @Test
        @DisplayName("Should propagate exception when refresh token revocation fails")
        void shouldPropagateExceptionWhenRevocationFails() {
            String invalidToken = "invalid-token";
            willThrow(new RuntimeException("Token not found or already revoked"))
                    .given(refreshTokenService).revokeRefreshToken(invalidToken);

            assertThatThrownBy(() -> authService.logout(invalidToken))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Token not found or already revoked");

            verify(refreshTokenService).revokeRefreshToken(invalidToken);
        }
    }
}