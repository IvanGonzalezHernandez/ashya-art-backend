package com.ashyaart.ashya_art_backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashyaart.ashya_art_backend.repository.ClienteDao;
import com.ashyaart.ashya_art_backend.repository.SecretoCompraDao;
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
    private SecretoCompraDao secretoCompraDao;
    
    @Autowired
    private StripeService stripeService;

    @GetMapping("/totals")
    public Map<String, Object> getTotals() throws Exception {

        Map<String, Object> result = new HashMap<>();

        long totalClientes = clienteDao.count();
        long totalProductos = productoCompraDao.count();
        long totalTarjetasRegalo = tarjetaRegaloCompraDao.count();
        long totalSecretos = secretoCompraDao.count();
        long totalReservas = cursoCompraDao.count();
        long totalNewsletter = newsletterDao.count();
        
        //BigDecimal totalIngresos = compraDao.sumTotalPagado();
        BigDecimal totalIngresos = stripeService.calcularIngresosTotalesStripe();
        BigDecimal totalComisiones = stripeService.calcularComisionesStripe();
        BigDecimal totalIngresosNetos = totalIngresos.subtract(totalComisiones);

        Map<String, Long> pagos = stripeService.calcularPagosPorMetodo();
        Map<String, Object> ingresosPorMes = stripeService.calcularIngresosPorMesStripe();

        result.put("totalClientes", totalClientes);
        result.put("totalProductos", totalProductos);
        result.put("totalSecretos", totalSecretos);
        result.put("totalTarjetasRegalo", totalTarjetasRegalo);
        result.put("totalReservas", totalReservas);
        result.put("totalNewsletter", totalNewsletter);
        result.put("totalIngresos", totalIngresos != null ? totalIngresos : BigDecimal.ZERO);
        result.put("totalIngresosNetos", totalIngresosNetos != null ? totalIngresosNetos : BigDecimal.ZERO);
        result.put("totalComisiones", totalComisiones != null ? totalComisiones : BigDecimal.ZERO);

        result.put("totalPagos", pagos.get("totalPagos"));
        result.put("pagosTarjeta", pagos.get("pagosTarjeta"));
        result.put("pagosPaypal", pagos.get("pagosPaypal"));
        result.put("pagosOtros", pagos.get("pagosOtros"));

        result.putAll(ingresosPorMes);

        return result;
    }
}
