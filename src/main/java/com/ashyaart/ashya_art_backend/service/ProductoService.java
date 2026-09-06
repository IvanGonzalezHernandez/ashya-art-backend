package com.ashyaart.ashya_art_backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ashyaart.ashya_art_backend.assembler.ProductoAssembler;
import com.ashyaart.ashya_art_backend.entity.Producto;
import com.ashyaart.ashya_art_backend.filter.ProductoFilter;
import com.ashyaart.ashya_art_backend.model.ProductoDto;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;

@Service
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    private static final String SUBFOLDER = "productos";

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private FileStorageService fileStorageService;

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
    public ProductoDto crearProducto(ProductoDto productoDto, MultipartFile img1, MultipartFile img2,
            MultipartFile img3, MultipartFile img4, MultipartFile img5) throws IOException {
        logger.info("crearProducto - Creando nuevo producto: {}", productoDto);
        Producto producto = ProductoAssembler.toEntity(productoDto);
        producto.setId(null);

        producto.setImg1Url(storeIfPresent(img1));
        producto.setImg2Url(storeIfPresent(img2));
        producto.setImg3Url(storeIfPresent(img3));
        producto.setImg4Url(storeIfPresent(img4));
        producto.setImg5Url(storeIfPresent(img5));

        Producto productoGuardado = productoDao.save(producto);
        ProductoDto dtoGuardado = ProductoAssembler.toDto(productoGuardado);
        logger.info("crearProducto - Producto creado con ID: {}", dtoGuardado.getId());
        return dtoGuardado;
    }

    @Transactional
    public ProductoDto actualizarProducto(ProductoDto productoDto, MultipartFile img1, MultipartFile img2,
            MultipartFile img3, MultipartFile img4, MultipartFile img5) throws IOException {
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

        // --- Imágenes: borrar -> elimina archivo y deja null; reemplazar -> borra la anterior y guarda la nueva; nada -> conserva ---
        producto.setImg1Url(mergeImagen(producto.getImg1Url(), img1, Boolean.TRUE.equals(productoDto.getDeleteImg1())));
        producto.setImg2Url(mergeImagen(producto.getImg2Url(), img2, Boolean.TRUE.equals(productoDto.getDeleteImg2())));
        producto.setImg3Url(mergeImagen(producto.getImg3Url(), img3, Boolean.TRUE.equals(productoDto.getDeleteImg3())));
        producto.setImg4Url(mergeImagen(producto.getImg4Url(), img4, Boolean.TRUE.equals(productoDto.getDeleteImg4())));
        producto.setImg5Url(mergeImagen(producto.getImg5Url(), img5, Boolean.TRUE.equals(productoDto.getDeleteImg5())));

        Producto guardado = productoDao.save(producto);
        return ProductoAssembler.toDto(guardado);
    }

    private String storeIfPresent(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            return fileStorageService.store(file, SUBFOLDER);
        }
        return null;
    }

    private String mergeImagen(String urlActual, MultipartFile nuevaImagen, boolean eliminar) throws IOException {
        if (nuevaImagen != null && !nuevaImagen.isEmpty()) {
            fileStorageService.delete(urlActual);
            return fileStorageService.store(nuevaImagen, SUBFOLDER);
        }
        if (eliminar) {
            fileStorageService.delete(urlActual);
            return null;
        }
        return urlActual;
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
