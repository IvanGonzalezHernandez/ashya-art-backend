package com.ashyaart.ashya_art_backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MigracionImagenesRunnerTest {

    private static byte[] bytes(int... valores) {
        byte[] b = new byte[16];
        for (int i = 0; i < valores.length; i++) b[i] = (byte) valores[i];
        return b;
    }

    @Test
    void reconoceWebp() {
        assertEquals("webp", MigracionImagenesRunner.extension(
                bytes('R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P')));
    }

    @Test
    void reconoceJpegPngYGif() {
        assertEquals("jpg", MigracionImagenesRunner.extension(bytes(0xFF, 0xD8, 0xFF, 0xE0)));
        assertEquals("png", MigracionImagenesRunner.extension(bytes(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A)));
        assertEquals("gif", MigracionImagenesRunner.extension(bytes('G', 'I', 'F', '8', '9', 'a')));
    }

    @Test
    void desconocidoCaeEnWebp() {
        assertEquals("webp", MigracionImagenesRunner.extension(bytes(1, 2, 3)));
        assertEquals("webp", MigracionImagenesRunner.extension(new byte[0]));
    }
}
