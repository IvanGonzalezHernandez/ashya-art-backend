package com.ashyaart.ashya_art_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/cursos/**").permitAll()
                .requestMatchers("/api/productos/**").permitAll()
                .requestMatchers("/api/tarjetas-regalo/**").permitAll()
                .requestMatchers("/api/clientes/**").permitAll()
                .requestMatchers("/api/newsletters/**").permitAll()
                .requestMatchers("/api/newsletters/suscribirse/**").permitAll()
                .requestMatchers("/api/cursos-compra/**").permitAll()
                .requestMatchers("/api/cursos-fecha/**").permitAll()
                .requestMatchers("/api/productos-compra/**").permitAll()
                .requestMatchers("/api/secretos/**").permitAll()
                .requestMatchers("/api/secretos-compra/**").permitAll()
                .requestMatchers("/api/carrito/**").permitAll()
                .requestMatchers("/stripe/webhook/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "https://ashya-art-frontend.onrender.com"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

