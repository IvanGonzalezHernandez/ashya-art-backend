package com.ashyaart.ashya_art_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;

@Service
public class StockService {

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private CursoFechaDao cursoDao;

    public boolean hayStockSuficiente(ItemCarritoDto item) {
        Integer stockDisponible;

        switch (item.getTipo().toUpperCase()) {
            case "PRODUCTO":
                stockDisponible = productoDao.obtenerStockPorId(item.getId());
                break;

            case "CURSO":
                stockDisponible = cursoDao.obtenerPlazasPorIdCursoFecha(item.getId());
                break;

            case "SECRETO":
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

