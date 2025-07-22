package com.ashyaart.ashya_art_backend.controller;

import java.util.List;

import com.ashyaart.ashya_art_backend.filter.ProductoCompraFilter;
import com.ashyaart.ashya_art_backend.model.ProductoCompraDto;
import com.ashyaart.ashya_art_backend.service.ProductoCompraService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos-compra")
public class ProductoCompraController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoCompraController.class);

    @Autowired
    private ProductoCompraService productoCompraService;

    @GetMapping
    public ResponseEntity<List<ProductoCompraDto>> findByFilter(ProductoCompraFilter filter) {
        logger.info("findByFilter - Solicitud GET para obtener compras de productos");
        List<ProductoCompraDto> comprasDto = productoCompraService.findByFilter(filter);
        logger.info("findByFilter - Se encontraron {} compras", comprasDto.size());
        return ResponseEntity.ok(comprasDto);
    }

    @PostMapping
    public ResponseEntity<ProductoCompraDto> crearProductoCompra(@RequestBody ProductoCompraDto productoCompraDto) {
        logger.info("crearProductoCompra - Solicitud POST para crear una nueva compra: {}", productoCompraDto);
        ProductoCompraDto nuevaCompra = productoCompraService.crearProductoCompra(productoCompraDto);
        logger.info("crearProductoCompra - Compra creada con ID: {}", nuevaCompra.getId());
        return ResponseEntity.ok(nuevaCompra);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoCompraDto> actualizarProductoCompra(@PathVariable Long id, @RequestBody ProductoCompraDto productoCompraDto) {
        logger.info("actualizarProductoCompra - Solicitud PUT para actualizar compra con ID {}: {}", id, productoCompraDto);
        productoCompraDto.setId(id);
        ProductoCompraDto compraActualizada = productoCompraService.actualizarProductoCompra(productoCompraDto);
        logger.info("actualizarProductoCompra - Compra actualizada con ID: {}", compraActualizada.getId());
        return ResponseEntity.ok(compraActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoCompra(@PathVariable Long id) {
        logger.info("eliminarProductoCompra - Solicitud DELETE para eliminar compra con ID: {}", id);
        productoCompraService.eliminarProductoCompra(id);
        logger.info("eliminarProductoCompra - Compra con ID {} eliminada (borrado lógico)", id);
        return ResponseEntity.noContent().build();
    }
}
