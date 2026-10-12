package com.ashyaart.ashya_art_backend.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;

class StockServiceTest {

    private final ProductoDao productoDao = mock(ProductoDao.class);
    private final StockService service = new StockService(productoDao, mock(CursoFechaDao.class));

    private static ItemCarritoDto producto(long id, int cantidad) {
        ItemCarritoDto item = new ItemCarritoDto();
        item.setId(id);
        item.setTipo("PRODUCTO");
        item.setNombre("Taza " + id);
        item.setCantidad(cantidad);
        return item;
    }

    @Test
    void aceptaUnCarritoDentroDelStock() {
        when(productoDao.obtenerStockPorId(1L)).thenReturn(3);
        assertDoesNotThrow(() -> service.validarCarrito(List.of(producto(1, 3))));
    }

    @Test
    void rechazaMasUnidadesQueStock() {
        when(productoDao.obtenerStockPorId(1L)).thenReturn(2);
        assertThrows(IllegalArgumentException.class, () -> service.validarCarrito(List.of(producto(1, 3))));
    }

    @Test
    void sumaLasLineasRepetidasDelMismoProducto() {
        when(productoDao.obtenerStockPorId(1L)).thenReturn(3);
        assertThrows(IllegalArgumentException.class,
                () -> service.validarCarrito(List.of(producto(1, 2), producto(1, 2))));
    }

    @Test
    void rechazaCantidadesCeroONegativas() {
        when(productoDao.obtenerStockPorId(1L)).thenReturn(5);
        assertThrows(IllegalArgumentException.class, () -> service.validarCarrito(List.of(producto(1, 0))));
        assertThrows(IllegalArgumentException.class, () -> service.validarCarrito(List.of(producto(1, -2))));
    }
}
