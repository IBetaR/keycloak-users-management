package com.ibetar.keycloak.security;

import com.ibetar.keycloak.jwt.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 * Configures security settings for the application, including JWT authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthConverter converter;
    /**
     * Constructs a new SecurityConfig instance with the specified JwtAuthConverter.
     *
     * @param converter The JwtAuthConverter used for JWT authentication.
     */
    public SecurityConfig(JwtAuthConverter converter) { this.converter = converter; }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http The HttpSecurity object used to configure security settings.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorize -> authorize
                        .anyRequest()
                        .authenticated());
        http.oauth2ResourceServer((rs) -> rs.jwt(
                        (jwt) -> jwt.jwtAuthenticationConverter(converter)));
        http.sessionManagement(
                session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
