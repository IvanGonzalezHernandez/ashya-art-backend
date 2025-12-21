package com.ashyaart.ashya_art_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ashyaart.ashya_art_backend.filter.TokenFilter;

import java.util.List;

@Configuration
public class SecurityConfig {
	
	/* Filtro que valida el token en cada request. */
	// Actualmente la lógica no está funcionando. Par que funcione en el front en cada peteicion que se haga se debe enviar el toke
	// Y después en las rutas del back con .authenticated() se valida si el token es correcto.
	// Para ellos también habra que separar laas rutas públicas de las privadas.
    @Autowired
    private TokenFilter tokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/cursos/**").permitAll()
                .requestMatchers("/api/productos/**").permitAll()
                .requestMatchers("/api/tarjetas-regalo/**").permitAll()
                .requestMatchers("/api/tarjetas-regalo-compra/**").permitAll()
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
                .requestMatchers("/api/firing/**").permitAll()
                .requestMatchers("/api/studio/**").permitAll()
                .requestMatchers("/api/errores/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().permitAll()
            );
        
        // Registramos el filtro que valida el token en cada request
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "https://ashya-art-frontend.onrender.com",
            "https://ashya-art.com",
            "https://www.ashya-art.com"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

