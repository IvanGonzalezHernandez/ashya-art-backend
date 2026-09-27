package com.ashyaart.ashya_art_backend.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ashyaart.ashya_art_backend.service.FileStorageService;

/**
 * Migración única de las imágenes guardadas como BLOB en la base de datos a archivos en disco
 * (ver scripts/sql/2026-09-06_migrar_imagenes_de_bbdd_a_archivos.sql).
 *
 * Solo se activa con MIGRAR_IMAGENES=true. Por cada imagen con BLOB y sin URL escribe el
 * archivo en UPLOAD_DIR y rellena la columna *_url. Es idempotente: las imágenes que ya tienen
 * URL se saltan, así que puede ejecutarse varias veces o relanzarse si se cortó a medias.
 * No borra los BLOB: eso se hace a mano con el PASO 2 del script SQL, una vez verificado.
 */
@Component
@ConditionalOnProperty(name = "app.migrar-imagenes", havingValue = "true")
public class MigracionImagenesRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(MigracionImagenesRunner.class);

    /** Tabla, subcarpeta (la misma que usa su servicio) y columnas BLOB a migrar. */
    private record Origen(String tabla, String subcarpeta, List<String> columnas) {}

    private static final List<Origen> ORIGENES = List.of(
            new Origen("curso", "cursos", List.of("img1", "img2", "img3", "img4", "img5")),
            new Origen("producto", "productos", List.of("img1", "img2", "img3", "img4", "img5")),
            new Origen("tarjeta_regalo", "tarjetas", List.of("img")));

    private final JdbcTemplate jdbc;
    private final FileStorageService fileStorageService;

    public MigracionImagenesRunner(JdbcTemplate jdbc, FileStorageService fileStorageService) {
        this.jdbc = jdbc;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Migración de imágenes: inicio, destino {}", fileStorageService.getRootLocation());
        int totalMigradas = 0;
        int totalFallos = 0;
        for (Origen origen : ORIGENES) {
            int migradas = 0;
            int fallos = 0;
            for (String columna : origen.columnas()) {
                if (!existeColumna(origen.tabla(), columna) || !existeColumna(origen.tabla(), columna + "_url")) {
                    logger.info("Migración de imágenes: {}.{} no existe, se salta", origen.tabla(), columna);
                    continue;
                }
                for (Long id : pendientes(origen.tabla(), columna)) {
                    try {
                        migrar(origen, columna, id);
                        migradas++;
                    } catch (Exception e) {
                        fallos++;
                        logger.error("Migración de imágenes: fallo en {}.{} id={}", origen.tabla(), columna, id, e);
                    }
                }
            }
            logger.info("Migración de imágenes: {}: {} migradas, {} fallos", origen.tabla(), migradas, fallos);
            totalMigradas += migradas;
            totalFallos += fallos;
        }
        logger.info("Migración de imágenes: fin, {} migradas, {} fallos. Quitar MIGRAR_IMAGENES cuando esté verificado.",
                totalMigradas, totalFallos);
    }

    private boolean existeColumna(String tabla, String columna) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() "
                        + "AND LOWER(TABLE_NAME) = ? AND LOWER(COLUMN_NAME) = ?",
                Integer.class, tabla, columna);
        return n != null && n > 0;
    }

    private List<Long> pendientes(String tabla, String columna) {
        // Tabla y columna vienen de ORIGENES, no de entrada externa.
        return jdbc.queryForList(
                "SELECT id FROM " + tabla + " WHERE " + columna + " IS NOT NULL AND " + columna + "_url IS NULL",
                Long.class);
    }

    private void migrar(Origen origen, String columna, Long id) throws IOException {
        byte[] bytes = jdbc.queryForObject(
                "SELECT " + columna + " FROM " + origen.tabla() + " WHERE id = ?", byte[].class, id);
        if (bytes == null || bytes.length == 0) {
            return;
        }

        Path carpeta = fileStorageService.getRootLocation().resolve(origen.subcarpeta());
        Files.createDirectories(carpeta);
        String nombre = UUID.randomUUID() + "." + extension(bytes);
        Path destino = carpeta.resolve(nombre);
        Files.write(destino, bytes);

        String url = "/uploads/" + origen.subcarpeta() + "/" + nombre;
        try {
            jdbc.update("UPDATE " + origen.tabla() + " SET " + columna + "_url = ? WHERE id = ? AND "
                    + columna + "_url IS NULL", url, id);
        } catch (RuntimeException e) {
            Files.deleteIfExists(destino);
            throw e;
        }
    }

    /** Deduce la extensión por los primeros bytes; en producción todas son WebP. */
    static String extension(byte[] b) {
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return "webp";
        }
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "png";
        }
        if (b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8') {
            return "gif";
        }
        return "webp";
    }
}
