package com.ashyaart.ashya_art_backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ClasificadorVisitaTest {

    @Test
    void normalizaLasRutasPublicas() {
        assertEquals(Optional.of("/"), ClasificadorVisita.ruta("/"));
        assertEquals(Optional.of("/workshops"), ClasificadorVisita.ruta("/workshops/"));
        assertEquals(Optional.of("/workshops/5-kintsugi-class"), ClasificadorVisita.ruta("/Workshops/5-kintsugi-class?utm_source=ig#top"));
        assertEquals(Optional.of("/gift-cards/3-gift-card"), ClasificadorVisita.ruta("/gift-cards/3-gift-card"));
    }

    @Test
    void descartaRutasQueNoSonPaginasPublicas() {
        assertTrue(ClasificadorVisita.ruta("/dashboard/inicio").isEmpty());
        assertTrue(ClasificadorVisita.ruta("/login").isEmpty());
        assertTrue(ClasificadorVisita.ruta("/maintenance").isEmpty());
        assertTrue(ClasificadorVisita.ruta("/no-existe").isEmpty());
        assertTrue(ClasificadorVisita.ruta("/workshops/a/b").isEmpty());
        assertTrue(ClasificadorVisita.ruta("/shop/<script>").isEmpty());
        assertTrue(ClasificadorVisita.ruta(null).isEmpty());
        assertTrue(ClasificadorVisita.ruta("/workshops/" + "x".repeat(200)).isEmpty());
    }

    @Test
    void clasificaElOrigen() {
        assertEquals("INTERNAL", ClasificadorVisita.origen(false, "https://www.google.com/", null));
        assertEquals("DIRECT", ClasificadorVisita.origen(true, "", null));
        assertEquals("DIRECT", ClasificadorVisita.origen(true, "https://ashya-art.com/workshops", null));
        assertEquals("GOOGLE", ClasificadorVisita.origen(true, "https://www.google.de/", null));
        assertEquals("SEARCH", ClasificadorVisita.origen(true, "https://www.bing.com/search?q=x", null));
        assertEquals("INSTAGRAM", ClasificadorVisita.origen(true, "https://l.instagram.com/", null));
        assertEquals("FACEBOOK", ClasificadorVisita.origen(true, "https://m.facebook.com/", null));
        assertEquals("NEWSLETTER", ClasificadorVisita.origen(true, "https://mail.google.com/", null));
        assertEquals("OTHER", ClasificadorVisita.origen(true, "https://blog.example.org/post", null));
        assertEquals("NEWSLETTER", ClasificadorVisita.origen(true, "https://www.google.com/", "newsletter"));
        assertEquals("INSTAGRAM", ClasificadorVisita.origen(true, "", "instagram"));
    }

    @Test
    void clasificaElDispositivoYDescartaRobots() {
        assertEquals(Optional.of("MOBILE"), ClasificadorVisita.dispositivo(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 Mobile/15E148"));
        assertEquals(Optional.of("TABLET"), ClasificadorVisita.dispositivo(
                "Mozilla/5.0 (Linux; Android 13; SM-X200) AppleWebKit/537.36 Chrome/120.0 Safari/537.36"));
        assertEquals(Optional.of("DESKTOP"), ClasificadorVisita.dispositivo(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/120.0 Safari/537.36"));
        assertTrue(ClasificadorVisita.dispositivo("Mozilla/5.0 (compatible; Googlebot/2.1)").isEmpty());
        assertTrue(ClasificadorVisita.dispositivo("Mozilla/5.0 HeadlessChrome/120.0").isEmpty());
        assertTrue(ClasificadorVisita.dispositivo("").isEmpty());
    }

    @Test
    void normalizaElIdioma() {
        assertEquals("de", ClasificadorVisita.idioma("DE"));
        assertEquals("en", ClasificadorVisita.idioma("en-GB"));
        assertEquals("other", ClasificadorVisita.idioma("fr"));
        assertEquals("other", ClasificadorVisita.idioma(null));
    }
}
