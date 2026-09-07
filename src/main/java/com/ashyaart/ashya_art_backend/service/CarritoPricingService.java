package com.ashyaart.ashya_art_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ashyaart.ashya_art_backend.model.ItemCarritoDto;
import com.ashyaart.ashya_art_backend.repository.CursoFechaDao;
import com.ashyaart.ashya_art_backend.repository.ProductoDao;
import com.ashyaart.ashya_art_backend.repository.TarjetaRegaloDao;

/**
 * Calcula el precio de un item de carrito siempre a partir del precio vigente en BBDD
 * (Curso/Producto/TarjetaRegalo), nunca del precio que declara el propio carrito del
 * cliente: ese valor viaja en la petición y puede ser manipulado antes de crear la
 * sesión de Stripe, así que nunca debe usarse para calcular importes a cobrar.
 */
@Service
public class CarritoPricingService {

    @Autowired
    private ProductoDao productoDao;

    @Autowired
    private CursoFechaDao cursoFechaDao;

    @Autowired
    private TarjetaRegaloDao tarjetaRegaloDao;

    public BigDecimal precioUnitarioReal(ItemCarritoDto item) {
        switch (item.getTipo().toUpperCase()) {
            case "PRODUCTO":
                return productoDao.findById(item.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getId()))
                        .getPrecio();

            case "CURSO":
                return cursoFechaDao.findById(item.getId())
                        .orElseThrow(() -> new IllegalArgumentException("CursoFecha no encontrada: " + item.getId()))
                        .getCurso().getPrecio();

            case "TARJETA":
                return tarjetaRegaloDao.findById(item.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Tarjeta Regalo no encontrada: " + item.getId()))
                        .getPrecio();

            default:
                throw new IllegalArgumentException("Tipo de item desconocido: " + item.getTipo());
        }
    }

    public BigDecimal calcularTotalReal(List<ItemCarritoDto> items) {
        return items.stream()
                .map(i -> precioUnitarioReal(i).multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** El carrito necesita un metodo de entrega en cuanto contiene algun PRODUCTO. */
    public boolean requiereEnvio(List<ItemCarritoDto> items) {
        return items.stream().anyMatch(i -> "PRODUCTO".equalsIgnoreCase(i.getTipo()));
    }

    /**
     * Coste de envio segun el metodo elegido, calculado siempre en servidor (nunca a partir
     * de un importe que declare el cliente). Solo aplica cuando el carrito contiene algun
     * PRODUCTO; en otro caso el metodo es irrelevante y el coste es cero. Por ahora solo se
     * hacen envios a Alemania y al resto de la UE (mas recogida gratuita en el taller).
     */
    public BigDecimal costeEnvio(List<ItemCarritoDto> items, String metodoEnvio) {
        if (!requiereEnvio(items)) {
            return BigDecimal.ZERO;
        }

        if (metodoEnvio == null) {
            throw new IllegalArgumentException("Debe indicarse un metodo de entrega para los productos del carrito.");
        }

        return switch (metodoEnvio.toUpperCase()) {
            case "PICKUP" -> BigDecimal.ZERO;
            case "GERMANY" -> BigDecimal.valueOf(10);
            case "EU" -> BigDecimal.valueOf(20);
            default -> throw new IllegalArgumentException("Metodo de entrega no soportado: " + metodoEnvio);
        };
    }
}
