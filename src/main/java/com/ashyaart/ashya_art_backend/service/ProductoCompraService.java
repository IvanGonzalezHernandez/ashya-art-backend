package com.ashyaart.ashya_art_backend.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ashyaart.ashya_art_backend.assembler.ProductoCompraAssembler;
import com.ashyaart.ashya_art_backend.entity.Cliente;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.entity.ProductoCompra;
import com.ashyaart.ashya_art_backend.filter.ProductoCompraFilter;
import com.ashyaart.ashya_art_backend.model.ProductoCompraDto;
import com.ashyaart.ashya_art_backend.repository.ClienteDao;
import com.ashyaart.ashya_art_backend.repository.ProductoCompraDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;

@Service
public class ProductoCompraService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoCompraService.class);

    @Autowired
    private ProductoCompraDao productoCompraDao;

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private ProductoDao productoDao;

    public List<ProductoCompraDto> findByFilter(ProductoCompraFilter filter) {
        logger.info("findByFilter - Iniciando búsqueda de compras de productos");
        // Aquí asumo que el método findByFiltros recibe idCliente u otros filtros
        List<ProductoCompra> compras = productoCompraDao.findByFiltros(filter.getIdCliente());
        List<ProductoCompraDto> resultado = compras.stream()
                .map(ProductoCompraAssembler::toDto)
                .toList();
        logger.info("findByFilter - Se encontraron {} compras", resultado.size());
        return resultado;
    }

    @Transactional
    public ProductoCompraDto crearProductoCompra(ProductoCompraDto dto) {
        logger.info("crearProductoCompra - Creando nueva compra: {}", dto);
        ProductoCompra compra = new ProductoCompra();

        Cliente cliente = clienteDao.findById(dto.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
        Producto producto = productoDao.findById(dto.getIdProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + dto.getIdProducto()));

        compra.setCliente(cliente);
        compra.setProducto(producto);
        compra.setCantidad(dto.getCantidad());
        compra.setFechaCompra(dto.getFechaCompra());

        ProductoCompra guardado = productoCompraDao.save(compra);
        ProductoCompraDto dtoGuardado = ProductoCompraAssembler.toDto(guardado);
        logger.info("crearProductoCompra - Compra creada con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public ProductoCompraDto actualizarProductoCompra(ProductoCompraDto dto) {
        logger.info("actualizarProductoCompra - Actualizando compra con ID: {}", dto.getId());
        ProductoCompra compra = productoCompraDao.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada con ID: " + dto.getId()));

        Cliente cliente = clienteDao.findById(dto.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + dto.getIdCliente()));
        Producto producto = productoDao.findById(dto.getIdProducto())
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado con ID: " + dto.getIdProducto()));

        compra.setCliente(cliente);
        compra.setProducto(producto);
        compra.setCantidad(dto.getCantidad());
        compra.setFechaCompra(dto.getFechaCompra());

        ProductoCompra actualizada = productoCompraDao.save(compra);
        ProductoCompraDto dtoActualizada = ProductoCompraAssembler.toDto(actualizada);
        logger.info("actualizarProductoCompra - Compra actualizada con ID: {}", dtoActualizada.getId());
        return dtoActualizada;
    }

    @Transactional
    public void eliminarProductoCompra(Long id) {
        logger.info("eliminarProductoCompra - Intentando eliminar compra con ID: {}", id);
        if (!productoCompraDao.existsById(id)) {
            logger.warn("eliminarProductoCompra - Compra con ID {} no encontrada", id);
            throw new RuntimeException("Compra con id " + id + " no encontrada");
        }

        productoCompraDao.deleteById(id);
        logger.info("eliminarProductoCompra - Compra con ID {} eliminada correctamente", id);
    }
}
