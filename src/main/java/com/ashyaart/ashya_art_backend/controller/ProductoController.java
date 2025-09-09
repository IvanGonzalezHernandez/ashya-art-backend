package com.ashyaart.ashya_art_backend.controller;

import java.io.IOException;
import java.util.List;

import com.ashyaart.ashya_art_backend.filter.ProductoFilter;
import com.ashyaart.ashya_art_backend.model.CursoDto;
import com.ashyaart.ashya_art_backend.model.ProductoDto;
import com.ashyaart.ashya_art_backend.service.ProductoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoDto>> findByFilter(ProductoFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener productos");
        List<ProductoDto> productosDto = productoService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} productos", productosDto.size());
        return ResponseEntity.ok(productosDto);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoById(@PathVariable Long id) {
        logger.info("getProductoById - Solicitud GET para obtener producto con ID: {}", id);
        ProductoDto producto = productoService.obtenerProductoPorId(id);

        if (producto != null) {
            logger.info("getProductoById - Producto encontrado con ID: {}", id);
            return ResponseEntity.ok(producto);
        } else {
            logger.warn("getProductoById - Producto no encontrado con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDto> crearProducto(
            @RequestPart("producto") ProductoDto productoDto,
            @RequestPart(value = "img1", required = false) MultipartFile img1,
            @RequestPart(value = "img2", required = false) MultipartFile img2,
            @RequestPart(value = "img3", required = false) MultipartFile img3,
            @RequestPart(value = "img4", required = false) MultipartFile img4,
            @RequestPart(value = "img5", required = false) MultipartFile img5
    ) {
        logger.info("crearProducto(MULTIPART) - Solicitud POST para crear producto: {}", productoDto);
        try {
            if (img1 != null && !img1.isEmpty()) productoDto.setImg1(img1.getBytes());
            if (img2 != null && !img2.isEmpty()) productoDto.setImg2(img2.getBytes());
            if (img3 != null && !img3.isEmpty()) productoDto.setImg3(img3.getBytes());
            if (img4 != null && !img4.isEmpty()) productoDto.setImg4(img4.getBytes());
            if (img5 != null && !img5.isEmpty()) productoDto.setImg5(img5.getBytes());

            ProductoDto nuevoProducto = productoService.crearProducto(productoDto);
            logger.info("crearProducto(MULTIPART) - Producto creado con ID: {}", nuevoProducto.getId());
            return ResponseEntity.ok(nuevoProducto);

        } catch (IOException e) {
            logger.error("crearProducto(MULTIPART) - Error procesando imágenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDto> actualizarProducto(
            @PathVariable Long id,
            @RequestPart("producto") ProductoDto productoDto,
            @RequestPart(value = "img1", required = false) MultipartFile img1,
            @RequestPart(value = "img2", required = false) MultipartFile img2,
            @RequestPart(value = "img3", required = false) MultipartFile img3,
            @RequestPart(value = "img4", required = false) MultipartFile img4,
            @RequestPart(value = "img5", required = false) MultipartFile img5,
            @RequestParam(value = "deleteImg1", required = false, defaultValue = "false") boolean deleteImg1,
            @RequestParam(value = "deleteImg2", required = false, defaultValue = "false") boolean deleteImg2,
            @RequestParam(value = "deleteImg3", required = false, defaultValue = "false") boolean deleteImg3,
            @RequestParam(value = "deleteImg4", required = false, defaultValue = "false") boolean deleteImg4,
            @RequestParam(value = "deleteImg5", required = false, defaultValue = "false") boolean deleteImg5
    ) {
        logger.info("actualizarProducto(MULTIPART) - PUT ID {}: {}", id, productoDto);
        try {
            productoDto.setId(id);

            // Pasar bytes de reemplazo (si se enviaron)
            if (img1 != null && !img1.isEmpty()) productoDto.setImg1(img1.getBytes());
            if (img2 != null && !img2.isEmpty()) productoDto.setImg2(img2.getBytes());
            if (img3 != null && !img3.isEmpty()) productoDto.setImg3(img3.getBytes());
            if (img4 != null && !img4.isEmpty()) productoDto.setImg4(img4.getBytes());
            if (img5 != null && !img5.isEmpty()) productoDto.setImg5(img5.getBytes());

            // Pasar flags de borrado al DTO (merge se hace en el service)
            productoDto.setDeleteImg1(deleteImg1);
            productoDto.setDeleteImg2(deleteImg2);
            productoDto.setDeleteImg3(deleteImg3);
            productoDto.setDeleteImg4(deleteImg4);
            productoDto.setDeleteImg5(deleteImg5);

            ProductoDto actualizado = productoService.actualizarProducto(productoDto);
            logger.info("actualizarProducto(MULTIPART) - Actualizado ID: {}", actualizado.getId());
            return ResponseEntity.ok(actualizado);

        } catch (IOException e) {
            logger.error("actualizarProducto(MULTIPART) - Error procesando imágenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping(value = "/{id}/imagen/{slot}")
    public ResponseEntity<byte[]> getImagenSlot(@PathVariable Long id, @PathVariable int slot) {
        ProductoDto p = productoService.obtenerProductoPorId(id);
        if (p == null || slot < 1 || slot > 5) return ResponseEntity.notFound().build();

        byte[] data = switch (slot) {
            case 1 -> p.getImg1();
            case 2 -> p.getImg2();
            case 3 -> p.getImg3();
            case 4 -> p.getImg4();
            case 5 -> p.getImg5();
            default -> null;
        };
        if (data == null || data.length == 0) return ResponseEntity.notFound().build();

        return ResponseEntity
            .ok()
            .contentType(org.springframework.http.MediaType.IMAGE_JPEG) // o detecta tipo si lo necesitas
            .body(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("eliminarProducto - Solicitud DELETE para eliminar producto con ID: {}", id);
        productoService.eliminarProducto(id);
        logger.info("eliminarProducto - Producto con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
