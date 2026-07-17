package com.gxssvp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration properties for JWT tokens, including secret and expiration times.
 *
 * @author Michael Terletskyi
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@PropertySource("classpath:application.properties")
@Data
public class JwtProperties {
    private String secret;
    private long accessTokenExpirationMs;
    private long refreshTokenExpirationMs;
}