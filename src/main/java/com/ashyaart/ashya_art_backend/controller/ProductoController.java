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


    @PostMapping
    public ResponseEntity<ProductoDto> crearProducto(@RequestBody ProductoDto productoDto) {
        logger.info("crearProducto - Solicitud POST para crear un nuevo producto: {}", productoDto);
        ProductoDto nuevoProducto = productoService.crearProducto(productoDto);
        logger.info("crearProducto - Producto creado con ID: {}", nuevoProducto.getId());
        return ResponseEntity.ok(nuevoProducto);
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDto> crearProducto(
            @RequestPart("producto") ProductoDto productoDto,
            @RequestPart(value = "imagenes", required = false) MultipartFile[] imagenes) {

        logger.info("crearProducto - Solicitud POST para crear un nuevo producto: {}", productoDto);

        try {
            if (imagenes != null) {
                if (imagenes.length > 5) {
                    return ResponseEntity.badRequest().body(null);
                }

                for (int i = 0; i < imagenes.length; i++) {
                    byte[] contenido = imagenes[i].getBytes();
                    switch (i) {
                        case 0 -> productoDto.setImg1(contenido);
                        case 1 -> productoDto.setImg2(contenido);
                        case 2 -> productoDto.setImg3(contenido);
                        case 3 -> productoDto.setImg4(contenido);
                        case 4 -> productoDto.setImg5(contenido);
                    }
                }
            }

            ProductoDto nuevoProducto = productoService.crearProducto(productoDto);
            logger.info("crearProducto - Producto creado con ID: {}", nuevoProducto.getId());
            return ResponseEntity.ok(nuevoProducto);

        } catch (IOException e) {
            logger.error("Error procesando las imágenes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> actualizarProducto(@PathVariable Long id, @RequestBody ProductoDto productoDto) {
        logger.info("actualizarProducto - Solicitud PUT para actualizar producto con ID {}: {}", id, productoDto);
        productoDto.setId(id);
        ProductoDto productoActualizado = productoService.actualizarProducto(productoDto);
        logger.info("actualizarProducto - Producto actualizado con ID: {}", productoActualizado.getId());
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("eliminarProducto - Solicitud DELETE para eliminar producto con ID: {}", id);
        productoService.eliminarProducto(id);
        logger.info("eliminarProducto - Producto con ID {} eliminado (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
