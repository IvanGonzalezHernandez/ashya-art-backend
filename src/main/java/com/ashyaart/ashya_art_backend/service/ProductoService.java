package com.ashyaart.ashya_art_backend.service;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.ProductoAssembler;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.filter.ProductoFilter;
import com.ashyaart.ashya_art_backend.model.ProductoDto;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;

@Service
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    @Autowired
    private ProductoDao productoDao;

    public List<ProductoDto> findByFilter(ProductoFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de productos");
        List<Producto> productos = productoDao.findByFiltros(filter.getNombre());
        List<ProductoDto> resultado = productos.stream()
                .map(ProductoAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} productos", resultado.size());
        return resultado;
    }
    
    public ProductoDto obtenerProductoPorId(Long id) {
        logger.info("obtenerProductoPorId - Buscando producto con ID: {}", id);
        Optional<Producto> productoOpt = productoDao.findById(id);

        if (productoOpt.isPresent()) {
            ProductoDto dto = ProductoAssembler.toDto(productoOpt.get());
            logger.info("obtenerProductoPorId - Producto encontrado con ID: {}", id);
            return dto;
        } else {
            logger.warn("obtenerProductoPorId - Producto con ID {} no encontrado", id);
            return null;
        }
    }


    @Transactional
    public ProductoDto crearProducto(ProductoDto productoDto) {
        logger.info("crearProducto - Creando nuevo producto: {}", productoDto);
        Producto producto = ProductoAssembler.toEntity(productoDto);
        producto.setId(null);
        Producto productoGuardado = productoDao.save(producto);
        ProductoDto dtoGuardado = ProductoAssembler.toDto(productoGuardado);
        logger.info("crearProducto - Producto creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public ProductoDto actualizarProducto(ProductoDto productoDto) {
        logger.info("actualizarProducto - Actualizando producto con ID: {}", productoDto.getId());

        Producto producto = productoDao.findById(productoDto.getId())
            .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + productoDto.getId()));

        // --- Campos normales ---
        producto.setNombre(productoDto.getNombre());
        producto.setSubtitulo(productoDto.getSubtitulo());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setPrecio(productoDto.getPrecio());
        producto.setStock(productoDto.getStock());
        producto.setFechaBaja(productoDto.getFechaBaja());

        producto.setCategoria(productoDto.getCategoria());
        producto.setMedidas(productoDto.getMedidas());
        producto.setMaterial(productoDto.getMaterial());

        // --- Imágenes: borrar -> null; reemplazar -> bytes; conservar -> valor actual ---
        // Borrados explícitos (como en CursoService)
        if (Boolean.TRUE.equals(productoDto.getDeleteImg1())) producto.setImg1(null);
        if (Boolean.TRUE.equals(productoDto.getDeleteImg2())) producto.setImg2(null);
        if (Boolean.TRUE.equals(productoDto.getDeleteImg3())) producto.setImg3(null);
        if (Boolean.TRUE.equals(productoDto.getDeleteImg4())) producto.setImg4(null);
        if (Boolean.TRUE.equals(productoDto.getDeleteImg5())) producto.setImg5(null);

        // Reemplazos (si llegaron bytes nuevos tienen prioridad)
        if (productoDto.getImg1() != null) producto.setImg1(productoDto.getImg1());
        if (productoDto.getImg2() != null) producto.setImg2(productoDto.getImg2());
        if (productoDto.getImg3() != null) producto.setImg3(productoDto.getImg3());
        if (productoDto.getImg4() != null) producto.setImg4(productoDto.getImg4());
        if (productoDto.getImg5() != null) producto.setImg5(productoDto.getImg5());

        Producto guardado = productoDao.save(producto);
        return ProductoAssembler.toDto(guardado);
    }

    @Transactional
    public void eliminarProducto(Long id) {
        logger.info("eliminarProducto - Intentando eliminar producto con ID: {}", id);
        if (!productoDao.existsById(id)) {
            logger.warn("eliminarProducto - Producto con ID {} no encontrado", id);
            throw new RuntimeException("Producto con id " + id + " no encontrado");
        }
        Integer filasAfectadas = productoDao.borradoLogico(id);
        if (filasAfectadas == 0) {
            logger.error("eliminarProducto - No se pudo eliminar el producto con ID: {}", id);
            throw new RuntimeException("No se pudo eliminar el producto con id " + id);
        }
        logger.info("eliminarProducto - Producto con ID {} eliminado correctamente (borrado lógico)", id);
    }
}
