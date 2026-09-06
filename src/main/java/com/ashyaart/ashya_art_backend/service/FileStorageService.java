package com.ashyaart.ashya_art_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda imágenes subidas (productos, cursos, tarjetas regalo) como archivos en disco,
 * en vez de como BLOB en la base de datos. Devuelve una URL relativa (servida mediante
 * el resource handler registrado en WebConfig) que se persiste en la entidad/DTO.
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private static final String PUBLIC_PREFIX = "/uploads/";

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public Path getRootLocation() {
        return rootLocation;
    }

    /**
     * Guarda el archivo subido dentro de {@code subfolder} con un nombre único.
     * Devuelve la URL relativa pública (p.ej. "/uploads/productos/&lt;uuid&gt;.webp").
     */
    public String store(MultipartFile file, String subfolder) throws IOException {
        Path targetDir = rootLocation.resolve(subfolder).normalize();
        Files.createDirectories(targetDir);

        String filename = UUID.randomUUID() + "." + extensionOf(file.getOriginalFilename());
        Path target = targetDir.resolve(filename);

        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return PUBLIC_PREFIX + subfolder + "/" + filename;
    }

    /** Borra el archivo asociado a una URL relativa devuelta por {@link #store}, si existe. */
    public void delete(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank() || !publicUrl.startsWith(PUBLIC_PREFIX)) {
            return;
        }
        Path target = rootLocation.resolve(publicUrl.substring(PUBLIC_PREFIX.length())).normalize();
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            logger.warn("No se pudo borrar el archivo {}", target, e);
        }
    }

    private String extensionOf(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        }
        return "webp";
    }
}
