
package com.unicap.pi3.security;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UsuarioAtivoFilter usuarioAtivoFilter;

    public SecurityConfig(UsuarioAtivoFilter usuarioAtivoFilter) {
        this.usuarioAtivoFilter = usuarioAtivoFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:3000")
        );

        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
            List.of("Authorization", "Content-Type", "Accept")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .cors(Customizer.withDefaults())

            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health").permitAll()
                .requestMatchers(
                    HttpMethod.OPTIONS,
                    "/**"
                ).permitAll()
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/usuarios/cadastro"
                ).permitAll()
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )

            .addFilterAfter(
                usuarioAtivoFilter,
                BearerTokenAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<UsuarioAtivoFilter>
            usuarioAtivoFilterRegistration(UsuarioAtivoFilter filter) {

        FilterRegistrationBean<UsuarioAtivoFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }
}
