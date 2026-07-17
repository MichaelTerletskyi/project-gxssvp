package com.gxssvp.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import com.gxssvp.entities.User;
import com.gxssvp.repositories.UserRepository;
import com.gxssvp.exceptions.UserLoginException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * This is my custom AuthenticationProvider. It gives more clarity and consistency when authentication fails.
 *
 * @author Michael Terletskyi
 */
@Log4j2
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationProviderImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Custom logic with authenticate request.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = Objects.requireNonNull(authentication.getCredentials()).toString();
        log.info("Attempt to authenticate User '{}'", username);

        final String userLoginFailedMsg = "User login failed";
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserLoginException(userLoginFailedMsg,
                        Map.of("username", String.format("User with username '%s' has not been found", username)))
                );

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Password for User '{}' does not match.", user);
            throw new UserLoginException(userLoginFailedMsg, Map.of("password", "Password is not correct"));
        }

        final List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPasswordHash(),
                        authorities
                ),
                password,
                authorities
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}