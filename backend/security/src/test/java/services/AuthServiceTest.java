package services;

import com.gxssvp.services.AuthService;
import com.gxssvp.services.RefreshTokenService;
import lombok.extern.log4j.Log4j2;
import com.gxssvp.entities.RefreshToken;
import com.gxssvp.entities.Role;
import com.gxssvp.entities.User;
import com.gxssvp.repositories.UserRepository;
import com.gxssvp.dtos.AuthResponse;
import com.gxssvp.dtos.RegisterRequest;
import com.gxssvp.jwt.JwtTokenProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test of Service handling authentication, registration, and token lifecycle operations.
 * It checks how Manages access and refresh token generation, validation, and revocation.
 *
 * @author Michael Terletskyi
 */
@Log4j2
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    @ParameterizedTest
    @CsvFileSource(resources = "/services/registerRequestsValidCases.csv", numLinesToSkip = 1)
    void shouldRegisterAllCasesAreValid(final String username, final String email, final String password) {
        final RegisterRequest request = new RegisterRequest();
                request.setUsername(username);
                request.setEmail(email);
                request.setPassword(password);

        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        final User mockSavedUser = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .role(Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

        when(jwtTokenProvider.generateAccessToken(any(Authentication.class))).thenReturn("mocked-jwt-token");

        RefreshToken mockRefreshToken = new RefreshToken();
        mockRefreshToken.setToken("mocked-refresh-token");
        when(refreshTokenService.createRefreshToken(username)).thenReturn(mockRefreshToken);

        final AuthResponse registered = authService.register(request);

        assertNotNull(registered);
        assertEquals(username, registered.getUsername());
        assertEquals(email, registered.getEmail());
        assertEquals(Role.USER.toString(), registered.getRole());
        assertThat(registered.getRefreshToken()).isNotBlank();
        assertThat(registered.getAccessToken()).isNotBlank();

        verify(userRepository, times(1)).save(any(User.class));
    }
}