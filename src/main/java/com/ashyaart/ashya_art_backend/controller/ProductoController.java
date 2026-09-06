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
            ProductoDto nuevoProducto = productoService.crearProducto(productoDto, img1, img2, img3, img4, img5);
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

            // Flags de borrado al DTO (merge con reemplazo/borrado se hace en el service)
            productoDto.setDeleteImg1(deleteImg1);
            productoDto.setDeleteImg2(deleteImg2);
            productoDto.setDeleteImg3(deleteImg3);
            productoDto.setDeleteImg4(deleteImg4);
            productoDto.setDeleteImg5(deleteImg5);

            ProductoDto actualizado = productoService.actualizarProducto(productoDto, img1, img2, img3, img4, img5);
            logger.info("actualizarProducto(MULTIPART) - Actualizado ID: {}", actualizado.getId());
            return ResponseEntity.ok(actualizado);

        } catch (IOException e) {
            logger.error("actualizarProducto(MULTIPART) - Error procesando imágenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("eliminarProducto - Solicitud DELETE para eliminar producto con ID: {}", id);
        productoService.eliminarProducto(id);
        logger.info("eliminarProducto - Producto con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
