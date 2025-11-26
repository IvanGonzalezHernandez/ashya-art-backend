package com.ashyaart.ashya_art_backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.repository.ClienteDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CompraDao;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private CursoCompraDao cursoCompraDao;

    @Autowired
    private CompraDao compraDao;

    @GetMapping("/totals")
    public Map<String, Object> getTotals() {
        Map<String, Object> result = new HashMap<>();

        long totalClientes = clienteDao.count();
        long totalProductos = productoDao.count();
        long totalReservas = cursoCompraDao.count();
        BigDecimal totalIngresos = compraDao.sumTotalPagado();

        result.put("totalClientes", totalClientes);
        result.put("totalProductos", totalProductos);
        result.put("totalReservas", totalReservas);
        result.put("totalIngresos", totalIngresos != null ? totalIngresos : BigDecimal.ZERO);

        return result;
    }
}
