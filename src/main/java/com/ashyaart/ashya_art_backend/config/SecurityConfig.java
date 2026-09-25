package com.ashyaart.ashya_art_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
	
	/* Filtro que valida el token firmado en cada request; las rutas .authenticated() exigen uno válido. */
    @Autowired
    private TokenFilter tokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 🔓 PUBLICO (cursos)
                .requestMatchers(HttpMethod.GET, "/api/cursos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/cursos/solicitud-curso").permitAll()
                // 🔒 PRIVADO (cursos)
                .requestMatchers(HttpMethod.POST, "/api/cursos").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/cursos/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/cursos/**").authenticated()

                // 🔓 PUBLICO (productos)
                .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                // 🔒 PRIVADO (productos)
                .requestMatchers(HttpMethod.POST, "/api/productos").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").authenticated()

                // 🔓 PUBLICO (tarjetas regalo)
                .requestMatchers(HttpMethod.GET, "/api/tarjetas-regalo").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tarjetas-regalo/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tarjetas-regalo/*/imagen").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tarjetas-regalo/validar").permitAll()

                // 🔒 PRIVADO (tarjetas regalo)
                .requestMatchers(HttpMethod.POST, "/api/tarjetas-regalo").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/tarjetas-regalo/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/tarjetas-regalo/**").authenticated()
                
                // 🔒 PRIVADOS (Tarjeta regalo compra)
                .requestMatchers(HttpMethod.GET, "/api/tarjetas-regalo-compra").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/tarjetas-regalo-compra/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/tarjetas-regalo-compra/**").authenticated()

                //🔒 PRIVADOS (Cliente)
                .requestMatchers(HttpMethod.GET, "/api/clientes").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/clientes").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/clientes/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").authenticated()
                
                // ✅ PUBLICOS (Newsletter)
                .requestMatchers(HttpMethod.POST, "/api/newsletters/suscribirse").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/newsletters/unsubscribe").permitAll()

                // 🔒 PRIVADOS (Newsletter)
                .requestMatchers(HttpMethod.GET, "/api/newsletters").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/newsletters").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/newsletters/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/newsletters/**").authenticated()
                
                // 🔒 PRIVADOS: (Curso compra)
                .requestMatchers(HttpMethod.POST, "/api/cursos-compra").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/cursos-compra").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/cursos-compra/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/cursos-compra/**").authenticated()

                // 🔓 PUBLICO (fechas de curso)
                .requestMatchers(HttpMethod.GET, "/api/cursos-fecha/**").permitAll()
                // 🔒 PRIVADO (fechas de curso)
                .requestMatchers(HttpMethod.POST, "/api/cursos-fecha").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/cursos-fecha/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/cursos-fecha/**").authenticated()

                .requestMatchers("/api/productos-compra/**").permitAll()
                .requestMatchers("/api/carrito/**").permitAll()

                // 🔓 PUBLICO (config: el sitio necesita saber si está en mantenimiento)
                .requestMatchers(HttpMethod.GET, "/api/config/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/config/mantenimiento/desbloquear").permitAll()
                // 🔒 PRIVADO (config: solo el admin puede cambiarla)
                .requestMatchers(HttpMethod.PUT, "/api/config/**").authenticated()
                .requestMatchers("/stripe/webhook/**").permitAll()
                .requestMatchers("/api/firing/**").permitAll()
                .requestMatchers("/api/studio/**").permitAll()
                .requestMatchers("/api/admin/**").authenticated()
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
            "https://ashya-art-frontend-pro.onrender.com",
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

