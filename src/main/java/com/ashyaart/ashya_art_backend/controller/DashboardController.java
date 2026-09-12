package com.ashyaart.ashya_art_backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.repository.ClienteDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloCompraDao;
import com.ashyaart.ashya_art_backend.service.StripeService;
import com.ashyaart.ashya_art_backend.repository.CursoCompraDao;
import com.ashyaart.ashya_art_backend.repository.NewsletterDao;
import com.ashyaart.ashya_art_backend.repository.ProductoCompraDao;
import com.ashyaart.ashya_art_backend.repository.CompraDao;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private ClienteDao clienteDao;

    @Autowired
    private ProductoCompraDao productoCompraDao;

    @Autowired
    private CursoCompraDao cursoCompraDao;

    @Autowired
    private CompraDao compraDao;
    
    @Autowired
    private TarjetaRegaloCompraDao tarjetaRegaloCompraDao;
    
    @Autowired
    private NewsletterDao newsletterDao;

    @Autowired
    private StripeService stripeService;

    @GetMapping("/totals")
    public Map<String, Object> getTotals() throws Exception {

        Map<String, Object> result = new HashMap<>();

        long totalClientes = clienteDao.count();
        long totalProductos = productoCompraDao.count();
        long totalTarjetasRegalo = tarjetaRegaloCompraDao.count();
        long totalReservas = cursoCompraDao.count();
        long totalNewsletter = newsletterDao.count();
        long reservasOnline = cursoCompraDao.countByCompra_PagadoTrue();
        long reservasAtelier = cursoCompraDao.countByCompra_PagadoFalse();
        long tarjetasRegaloCanjeadas = tarjetaRegaloCompraDao.countByCanjeadaTrue();
        long tarjetasRegaloNoCanjeadas = tarjetaRegaloCompraDao.countByCanjeadaFalse();

        //BigDecimal totalIngresos = compraDao.sumTotalPagado();
        Map<String, Object> estadisticasStripe = stripeService.calcularEstadisticasStripe();

        result.put("totalClientes", totalClientes);
        result.put("totalProductos", totalProductos);
        result.put("totalTarjetasRegalo", totalTarjetasRegalo);
        result.put("totalReservas", totalReservas);
        result.put("totalNewsletter", totalNewsletter);
        result.put("reservasOnline", reservasOnline);
        result.put("reservasAtelier", reservasAtelier);
        result.put("tarjetasRegaloCanjeadas", tarjetasRegaloCanjeadas);
        result.put("tarjetasRegaloNoCanjeadas", tarjetasRegaloNoCanjeadas);
        result.putAll(estadisticasStripe);

        return result;
    }
}
