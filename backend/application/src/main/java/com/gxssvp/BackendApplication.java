package com.gxssvp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry points of backend app.
 *
 * @author Michael Terletskyi
 */
@SpringBootApplication
@EnableJpaAuditing
public class BackendApplication {
    static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}