package com.ashyaart.ashya_art_backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;

@Service
public class StockService {

    private final ProductoDao productoDao;
    private final CursoFechaDao cursoDao;

    public StockService(ProductoDao productoDao, CursoFechaDao cursoDao) {
        this.productoDao = productoDao;
        this.cursoDao = cursoDao;
    }

    /**
     * Valida el carrito completo antes de cobrar: cada línea con cantidad positiva y, por cada
     * producto o fecha de curso, la suma de todas sus líneas dentro del stock/plazas disponibles.
     * Comprobar línea a línea no basta: dos líneas del mismo producto podían pasar cada una por
     * separado y entre las dos superar el stock.
     *
     * @throws IllegalArgumentException con el nombre del item que no cabe
     */
    public void validarCarrito(List<ItemCarritoDto> items) {
        Map<String, ItemCarritoDto> agrupados = new LinkedHashMap<>();
        for (ItemCarritoDto item : items) {
            if (item.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad no válida para: " + item.getNombre());
            }
            String clave = item.getTipo().toUpperCase() + "-" + item.getId();
            ItemCarritoDto acumulado = agrupados.get(clave);
            if (acumulado == null) {
                acumulado = new ItemCarritoDto();
                acumulado.setId(item.getId());
                acumulado.setTipo(item.getTipo());
                acumulado.setNombre(item.getNombre());
                agrupados.put(clave, acumulado);
            }
            acumulado.setCantidad(acumulado.getCantidad() + item.getCantidad());
        }

        for (ItemCarritoDto item : agrupados.values()) {
            if (!hayStockSuficiente(item)) {
                throw new IllegalArgumentException("No hay suficiente stock o plazas para: " + item.getNombre());
            }
        }
    }

    public boolean hayStockSuficiente(ItemCarritoDto item) {
        Integer stockDisponible;

        switch (item.getTipo().toUpperCase()) {
            case "PRODUCTO":
                stockDisponible = productoDao.obtenerStockPorId(item.getId());
                break;

            case "CURSO":
                stockDisponible = cursoDao.obtenerPlazasPorIdCursoFecha(item.getId());
                break;

            case "TARJETA":
                // Sin límite, cualquier cantidad está permitida
                return true;

            default:
                throw new IllegalArgumentException("Tipo de item desconocido: " + item.getTipo());
        }

        if (stockDisponible == null) {
            throw new IllegalArgumentException("No se encontró stock/plazas para: " + item.getNombre());
        }

        return item.getCantidad() <= stockDisponible;
    }
}
