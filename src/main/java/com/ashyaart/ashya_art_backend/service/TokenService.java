package com.ashyaart.ashya_art_backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Genera y valida los tokens de sesión de administrador: email + fecha de expiración,
 * firmados con HMAC-SHA256 para que no puedan falsificarse ni manipularse sin conocer
 * el secreto del servidor.
 */
@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final byte[] secretBytes;
    private final long expirationMillis;

    public TokenService(
            @Value("${auth.jwt-secret:}") String secretConfigurado,
            @Value("${auth.token-expiration-hours:12}") long expirationHoras
    ) {
        if (secretConfigurado == null || secretConfigurado.isBlank()) {
            byte[] aleatorio = new byte[32];
            new SecureRandom().nextBytes(aleatorio);
            secretConfigurado = Base64.getEncoder().encodeToString(aleatorio);
            logger.warn("AUTH_JWT_SECRET no configurado: generando uno aleatorio solo para esta ejecución. "
                    + "Todas las sesiones de administrador se invalidarán al reiniciar el backend. "
                    + "Configura la variable de entorno AUTH_JWT_SECRET en producción para evitar esto.");
        }
        this.secretBytes = secretConfigurado.getBytes(StandardCharsets.UTF_8);
        this.expirationMillis = expirationHoras * 3600_000L;
    }

    public String generarToken(String email) {
        long expiraEn = System.currentTimeMillis() + expirationMillis;
        String payload = email + ":" + expiraEn;
        String firma = firmar(payload);
        String raw = payload + ":" + firma;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /** Devuelve el email si el token tiene una firma válida y no ha caducado; vacío en cualquier otro caso. */
    public Optional<String> validarYObtenerEmail(String token) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] partes = decoded.split(":", 3);
            if (partes.length != 3) {
                return Optional.empty();
            }

            String email = partes[0];
            long expiraEn = Long.parseLong(partes[1]);
            String firmaRecibida = partes[2];

            String firmaEsperada = firmar(email + ":" + expiraEn);
            if (!MessageDigest.isEqual(
                    firmaRecibida.getBytes(StandardCharsets.UTF_8),
                    firmaEsperada.getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }

            if (System.currentTimeMillis() > expiraEn) {
                return Optional.empty();
            }

            return Optional.of(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String firmar(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token de sesión", e);
        }
    }
}
